package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.configuration.Configuration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
                else if (lanceMessage.getMessage().equals("Authentication required.")) {
                    out.println(new LanceMessage(
                        lanceMessage.getId(),
                        StatusCode.OK,
                        configuration.getPassword()
                    ));
                } else if (lanceMessage.getMessage().equals("Access granted.")) {
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

    public String getString(String key) {
        return get(key);
    }

    public Future<String> getStringAsync(String key) {
        return Executors
            .newSingleThreadExecutor()
            .submit(() -> getString(key));
    }

    public void setString(String key, String value) {
        set(key, value);
    }

    public boolean getBoolean(String key) {
        String value = get(key);

        if (value.equals("true") || value.equals("false"))
            return value.equals("true");
        else throw new NullPointerException("Not a boolean.");
    }

    public Future<Boolean> getBooleanAsync(String key) {
        return Executors
            .newSingleThreadExecutor()
            .submit(() -> getBoolean(key));
    }

    public void setBoolean(String key, boolean value) {
        set(key, Boolean.toString(value));
    }

    public int getInteger(String key) {
        String value = get(key);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NullPointerException("Not an integer.");
        }
    }

    public Future<Integer> getIntegerAsync(String key) {
        return Executors
            .newSingleThreadExecutor()
            .submit(() -> getInteger(key));
    }

    public void setInteger(String key, int value) {
        set(key, Integer.toString(value));
    }

    public float getFloat(String key) {
        String value = get(key);

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new NullPointerException("Not a float.");
        }
    }

    public Future<Float> getFloatAsync(String key) {
        return Executors
            .newSingleThreadExecutor()
            .submit(() -> getFloat(key));
    }

    public void setFloat(String key, float value) {
        set(key, Float.toString(value));
    }

    public byte[] getFile(String key) {
        String value = get(key, "getfile");

        return value.getBytes(StandardCharsets.UTF_8);
    }

    public Future<byte[]> getFileAsync(String key) {
        return Executors
            .newSingleThreadExecutor()
            .submit(() -> getFile(key));
    }

    public void setFile(String key, byte[] value) {
        set(key, new String(value), "setfile");
    }

    public boolean exists(String key) {
        return get(key, "exists").equals("true");
    }

    public boolean existsFile(String key) {
        return get(key, "existsfile").equals("true");
    }

    private String get(String key) {
        return get(key, "get");
    }
    
    private String get(String key, String command) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage(command + " " + key)
            .build()
            .toString()
        );

        return listenerManager.resolve(new ResolvableListener<>() {
            volatile LanceMessage value = null;

            @Override
            public String resolve() {
                while (value == null)
                    Thread.onSpinWait();

                return value.getMessage();
            }

            @Override
            public boolean run(String line) {
                LanceMessage lanceMessage = LanceMessage.getFromString(line);

                if (lanceMessage == null) {
                    out.close();

                    return false;
                }

                if (lanceMessage.getId() == id) {
                    value = lanceMessage;

                    return true;
                }

                return false;
            }
        });
    }

    private void set(String key, String value) {
        set(key, value, "set");
    }

    private void set(String key, String value, String command) {
        while (socket == null || out == null || in == null || listenerManager == null || !authorised)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage(command + " " + key + " " + value)
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
