package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.configuration.Configuration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.network.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

public class LanceClient extends Thread {
    private final Configuration configuration;
    private final String host;
    private final int port;

    public Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    public ListenerManager listenerManager;

    private int retries = 0;

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
        super("Lance-Client");

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
        } catch (IOException e) {
            if (retries++ < configuration.getMaxRetries() || configuration.getMaxRetries() == -1) {
                try {
                    Thread.sleep(configuration.getRetryTimeout());

                    System.out.println(e.getMessage() + " | Retrying..." + (configuration.getMaxRetries() == -1 ? "" : "(" + retries + "/" + configuration.getMaxRetries() + ")"));

                    run();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            } else e.printStackTrace();
        }
    }

    public void execute(String line) {
        while (socket == null || out == null || in == null || listenerManager == null)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessage(
            id,
            StatusCode.OK,
            new LanceString(line)
        ));
    }

    public String getString(String key) {
        return get(new LanceString(), key)
            .getObject()
            .getAsString();
    }

    public Future<LanceMessage> getStringAsync(String key) {
        return getAsync(new LanceString(), key);
    }

    public <T> LanceMessage get(LanceObject<T> lanceObject, String key) {
        while (socket == null || out == null || in == null || listenerManager == null)
            Thread.onSpinWait();

        int id = ThreadLocalRandom.current().nextInt();

        out.println(new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setObject(new LanceString("get " + key))
            .build()
            .toString()
        );

        return listenerManager.resolve(new ResolvableListener<>() {
            volatile LanceMessage value = null;

            @Override
            public LanceMessage resolve() {
                while (value == null)
                    Thread.onSpinWait();

                return value;
            }

            @Override
            public boolean run(String line) {
                LanceMessage lanceMessage = LanceMessage.getFromString(lanceObject, line);

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

    public <T> Future<LanceMessage> getAsync(LanceObject<T> lanceObject, String key) {
        return Executors
            .newSingleThreadExecutor()
            .submit(() -> get(lanceObject, key));
    }

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
