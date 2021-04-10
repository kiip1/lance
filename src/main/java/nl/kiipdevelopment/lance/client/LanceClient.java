package nl.kiipdevelopment.lance.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.configuration.Configuration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class LanceClient extends Thread implements AutoCloseable {
    private final Configuration configuration;
    private final String host;
    private final int port;

    public Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    public ListenerManager listenerManager;

    private final List<String> batchQueue = new ArrayList<>();

    private int retries = 0;
    private boolean authorised = false;
    private boolean batchMode = false;
    private StatusCode lastStatus = StatusCode.OK;
    private Consumer<LanceMessage> errorHandler = (code) -> {};
    private Consumer<InetAddress> completeCallback = (address) -> {};

    public LanceClient() {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, DefaultConfiguration.getDefaultConfiguration());
    }

    public LanceClient(Configuration configuration) {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, configuration);
    }

    public LanceClient(String host, int port) {
        this(host, port, DefaultConfiguration.getDefaultConfiguration());
    }

    public LanceClient(String host, int port, Configuration configuration) {
        this(host, port, configuration, "Lance-Client");
    }

    protected LanceClient(String host, int port, Configuration configuration, String threadName) {
        super(threadName);

        this.host = host;
        this.port = port;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            listenerManager = new ListenerManager(this, in);

            listenerManager.start();

            if (configuration.isPasswordEnabled()) listenerManager.listen(lanceMessage -> {
                if (lanceMessage == null)
                    out.close();
                else if (lanceMessage.getCode() == StatusCode.AUTH_REQUIRED) {
                    out.println(new LanceMessage(
                        lanceMessage.getId(),
                        StatusCode.OK,
                        configuration.getPassword()
                    ));
                } else if (lanceMessage.getCode() == StatusCode.ACCESS_GRANTED) {
                    authorised = true;
                    completeCallback.accept(socket.getLocalAddress());

                    return true;
                }

                return false;
            });
            else {
                authorised = true;
                completeCallback.accept(socket.getLocalAddress());
            }
        } catch (IOException e) {
            if (retries++ < configuration.getMaxRetries() || configuration.getMaxRetries() == -1) {
                try {
                    Thread.sleep(configuration.getRetryTimeout());

                    System.out.println("[" + getName() + "] " + e.getMessage() + " | Retrying..." + (configuration.getMaxRetries() == -1 ? "" : "(" + retries + "/" + configuration.getMaxRetries() + ")"));

                    run();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            } else e.printStackTrace();
        }
    }

    public void execute(String message) {
        execute(
            ThreadLocalRandom.current().nextInt(),
            message
        );
    }

    public void execute(int id, String message) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        out.println(new LanceMessage(
            id,
            StatusCode.OK,
            message.isBlank() ? "null" : message
        ));
    }

    public void execute(String message, JsonElement json) {
        execute(
            ThreadLocalRandom.current().nextInt(),
            message,
            json
        );
    }

    public void execute(int id, String message, JsonElement json) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        out.println(new LanceMessage(
            id,
            StatusCode.OK,
            message.isBlank() ? "null" : message ,
            json
        ));
    }
    
    /**
     * Writes the contents of a file. Be aware that if the server does have a
     * json based storage instead of file based, the string will be saved as json.
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path to the file
     * @param value The string value to write to the file
     * @return Whether the set was successful or not
     */
    public boolean setFile(String key, String value) {
        return set(key, value, null);
    }
    
    /**
     * Same as #setFile(String, String) but non-blocking, using a future.
     *
     * @param key The path to the file
     * @param value The string value to write to the file
     * @return A future for whether the set was successful or not
     */
    public CompletableFuture<Boolean> setFileAsync(String key, String value) {
        return CompletableFuture.supplyAsync(() -> setFile(key, value));
    }
    
    /**
     * Sets the value on the key. Be aware that if the server does have a
     * file based storage instead of json based, the json will be saved as string.
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path, separated by dots
     * @param value The json element to set
     * @return Whether the set was successful or not
     */
    public boolean setJson(String key, JsonElement value) {
        return set(key, null, value);
    }
    
    /**
     * Same as #setFile(String, JsonElement) but non-blocking, using a future.
     *
     * @param key The path to the file
     * @param value The string value to write to the file
     * @return A future for whether the set was successful or not
     */
    public CompletableFuture<Boolean> setJsonAsync(String key, JsonElement value) {
        return CompletableFuture.supplyAsync(() -> setJson(key, value));
    }
    
    /**
     * Retrieves the contents of a file. Be aware that if the server does have
     * a json based storage instead of file based, this will be null.
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path to the file
     * @return The contents of the file, or null if either the file is not found or the storage is json
     */
    public String getFile(String key) {
        return get(key).getAsFile();
    }
    
    /**
     * Retrieves a json element. Be aware that if the server does have a
     * a file based storage instead of json based, this will be null.
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path, separated by dots
     * @return The json element at the path, or null if either the path does not exist or the storage is files
     */
    public JsonElement getJson(String key) {
        return get(key).getAsJson();
    }
    
    /**
     * Same as #get(String) but non-blocking, using a future.
     *
     * @param key The path, separated by dots
     * @return A future for the DataValue at the path
     *
     * @see DataValue
     */
    public CompletableFuture<DataValue> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> get(key));
    }
    
    /**
     * Retrieves a DataValue, being a json element or a string (file contents).
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path, separated by dots
     * @return The DataValue at the path, even if it is null an empty DataValue will be returned
     *
     * @see DataValue
     */
    public DataValue get(String key) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        return listenerManager.resolve(new ResolvableListener<>(
            id,
            LanceMessage::asDataValue
        ), () -> out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage("get " + key)
            .build()
            .toString()
        ), errorHandler);
    }
    
    /**
     * Same as #exists(String) but non-blocking, using a future.
     *
     * @param key The path, separated by dots
     * @return A future for whether the key exists or not
     */
    public CompletableFuture<Boolean> existsAsync(String key) {
        return CompletableFuture.supplyAsync(() -> exists(key));
    }
    
    /**
     * Checks if a key exists. Works on both json and file based storage.
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path, separated by dots
     * @return Whether the key exists or not
     */
    public boolean exists(String key) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();
    
        int id = ThreadLocalRandom.current().nextInt();

        return listenerManager.resolve(new ResolvableListener<>(
            id,
            lanceMessage -> lanceMessage.getMessage().equals("true")
        ), () -> out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage("exists " + key)
            .build()
            .toString()
        ),  errorHandler);
    }
    
    /**
     * Same as #listFilenames() but non-blocking, using a future.
     *
     * @return A future for the list of filenames
     */
    public CompletableFuture<String[]> listFilenamesAsync() {
        return CompletableFuture.supplyAsync(this::listFilenames);
    }
    
    /**
     * Lists the files in the storage directory. Be aware that if the server
     * does have a json based storage instead of files, this will give an error.
     *
     * @return The list of filenames
     */
    public String[] listFilenames() {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();
        
        int id = ThreadLocalRandom.current().nextInt();
        
        return listenerManager.resolve(new ResolvableListener<>(
            id,
            (lanceMessage) -> {
                JsonArray array = lanceMessage.getJson().getAsJsonArray();
                String[] result = new String[array.size()];
                
                for (int i = 0; i < result.length; i++) {
                    JsonElement element = array.get(i);
                    result[i] = element.getAsString();
                }
                
                return result;
            }
        ), () -> out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage("list")
            .build()
            .toString()
        ), errorHandler);
    }

    /**
     * Adds invokes of the LanceClient#set method to a queue and then invoke them as a single command.
     * Recommended to use when invoking 1000 or more LanceClient#set methods at a time.
     *
     * @param runnable A runnable containing all the set actions.
     * @return Whether the batch was successful or not
     */
    public boolean batch(Runnable runnable) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        batchMode = true;

        int id = ThreadLocalRandom.current().nextInt();

        return listenerManager.resolve(new ResolvableListener<>(
            id,
            lanceMessage -> lanceMessage.getMessage().equals("true")
        ), () -> {
            try {
                Executors.newSingleThreadExecutor().submit(runnable).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            execute(
                id,
                "batch " + String.join("/", batchQueue)
            );

            batchQueue.clear();

            batchMode = false;
        }, errorHandler);
    }

    private boolean set(String key, String value, JsonElement json) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        if (batchMode) {
            batchQueue.add(new LanceMessageBuilder()
                .setId(id)
                .setStatusCode(StatusCode.OK)
                .setMessage("set " + key + " " + value)
                .setJson(json)
                .build().toString());

            return true;
        } else return listenerManager.resolve(new ResolvableListener<>(
                id,
                lanceMessage -> lanceMessage.getMessage().equals("true")
            ), () -> out.println(new LanceMessageBuilder()
                .setId(id)
                .setStatusCode(StatusCode.OK)
                .setMessage("set " + key + " " + value)
                .setJson(json)
                .build()
                .toString()
            ), errorHandler);
    }
    
    void setLastStatus(StatusCode lastStatus) {
        this.lastStatus = lastStatus;
    }
    
    public StatusCode getLastStatus() {
        return lastStatus;
    }
    
    public void setErrorHandler(Consumer<LanceMessage> errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public void setCompleteCallback(Consumer<InetAddress> completeCallback) {
        this.completeCallback = completeCallback;
    }
    
    @Override
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
            listenerManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
