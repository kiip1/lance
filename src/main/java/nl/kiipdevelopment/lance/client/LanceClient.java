package nl.kiipdevelopment.lance.client;

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
            listenerManager = new ListenerManager(in);

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

                    return true;
                }

                return false;
            });
            else authorised = true;
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

    public int execute(String message) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessage(
            id,
            StatusCode.OK,
            message
        ));

        return id;
    }

    public void execute(String message, JsonElement json) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessage(
            id,
            StatusCode.OK,
            message,
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
    public Future<Boolean> setFileAsync(String key, String value) {
        return Executors
                .newSingleThreadExecutor()
                .submit(() -> setFile(key, value));
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
    public Future<Boolean> setJsonAsync(String key, JsonElement value) {
        return Executors
                .newSingleThreadExecutor()
                .submit(() -> setJson(key, value));
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
    public Future<DataValue> getAsync(String key) {
        return Executors
                .newSingleThreadExecutor()
                .submit(() -> get(key));
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

        out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage("get " + key)
            .build()
            .toString()
        );
        
        try {
            return listenerManager.resolve(this, new ResolvableListener<>(id, out, LanceMessage::asDataValue));
        } catch (ErrorStatusException e) {
            errorHandler.accept(e.getLanceMessage());
            return new DataValue();
        }
    }
    
    /**
     * Same as #exists(String) but non-blocking, using a future.
     *
     * @param key The path, separated by dots
     * @return A future for whether the key exists or not
     */
    public Future<Boolean> existsAsync(String key) {
        return Executors
                .newSingleThreadExecutor()
                .submit(() -> exists(key));
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
    
        out.println(new LanceMessageBuilder()
                .setId(id)
                .setStatusCode(StatusCode.OK)
                .setMessage("exists " + key)
                .build()
                .toString()
        );
        
        try {
            return listenerManager.resolve(this, new ResolvableListener<>(id, out, (value) -> value.getMessage().equals("true")));
        } catch (ErrorStatusException e) {
            errorHandler.accept(e.getLanceMessage());
            return false;
        }
    }

    /**
     * Adds invokes of the LanceClient#set method to a queue and then invoke them as a single command.
     *
     * @param runnable A runnable containing all the set actions.
     * @return Whether the batch was successful or not
     */
    public boolean batch(Runnable runnable) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        boolean success = false;

        batchMode = true;

        try {
            Executors.newSingleThreadExecutor().submit(runnable).get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        int id = execute("batch " + String.join("/", batchQueue));

        try {
            success = listenerManager.resolve(this, new ResolvableListener<>(
                id,
                out,
                message -> Boolean.parseBoolean(message.getMessage())
            ));
        } catch (ErrorStatusException e) {
            e.printStackTrace();
        }

        batchQueue.clear();

        batchMode = false;

        return success;
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
        } else out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage("set " + key + " " + value)
            .setJson(json)
            .build()
            .toString());
        
        try {
            return listenerManager.resolve(this, new ResolvableListener<>(id, out, (returnValue) -> returnValue.getCode() == StatusCode.OK));
        } catch (ErrorStatusException e) {
            errorHandler.accept(e.getLanceMessage());

            return false;
        }
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
