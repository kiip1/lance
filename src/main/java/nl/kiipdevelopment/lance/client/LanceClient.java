package nl.kiipdevelopment.lance.client;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.configuration.Configuration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class LanceClient extends Thread implements AutoCloseable {
    private final Configuration configuration;
    private final String host;
    private final int port;

    public Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    public ListenerManager listenerManager;

    private int retries = 0;
    private boolean authorised = false;

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

            if (configuration.isPasswordEnabled()) listenerManager.listen(line -> {
                LanceMessage lanceMessage = LanceMessage.getFromString(line);

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

    public void execute(String line) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessage(
            id,
            StatusCode.OK,
            line
        ));
    }
    
    /**
     * Writes the contents of a file. Be aware that if the server does have a
     * json based storage instead of file based, the string will be saved as json.
     *
     * @param key The path to the file
     * @param value The string value to write to the file
     */
    public void setFile(String key, String value) {
        set(key, value, null);
    }
    
    /**
     * Sets the value on the key. Be aware that if the server does have a
     * file based storage instead of json based, the json will be saved as string.
     *
     * @param key The path, separated by dots
     * @param value The json element to set
     */
    public void set(String key, JsonElement value) {
        set(key, null, value);
    }
    
    /**
     * Retrieves the contents of a file. Be aware that if the server does have
     * a json based storage instead of file based, this will be null.
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
     *
     * @param key The path, separated by dots
     * @return The DataValue at the path
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

        return listenerManager.resolve(new ResolvableListener<>(id, out, LanceMessage::asDataValue));
    }
    
    /**
     * Checks if a key exists. Works on both json and file based storage.
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
    
        return listenerManager.resolve(new ResolvableListener<>(id, out, (value) -> value.getMessage().equals("true")));
    }

    private void set(String key, String value, JsonElement json) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage("set " + key + " " + value)
            .setJson(json)
            .build()
            .toString()
        );
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
