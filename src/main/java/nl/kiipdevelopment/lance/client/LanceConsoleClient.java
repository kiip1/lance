package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.configuration.Configuration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceString;

import java.util.Scanner;

public class LanceConsoleClient extends LanceClient {
    public LanceConsoleClient() {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, DefaultConfiguration.getDefaultConfiguration());
    }

    public LanceConsoleClient(Configuration configuration) {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, configuration);
    }

    public LanceConsoleClient(String host, int port) {
        this(host, port, DefaultConfiguration.getDefaultConfiguration());
    }

    public LanceConsoleClient(String host, int port, Configuration configuration) {
        super(host, port, configuration);

        init();
    }

    private void init() {
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                boolean active = true;

                while (socket == null || out == null || in == null || listenerManager == null)
                    Thread.onSpinWait();

                System.out.println("Console client started on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());

                listenerManager.listen(line -> {
                    LanceMessage lanceMessage = LanceMessage.getFromEncoded(new LanceString(), line);

                    if (lanceMessage == null) {
                        out.close();

                        return false;
                    }

                    System.out.println("[" + lanceMessage.getCode() + "] [" + lanceMessage.getId() + "] " + lanceMessage.getObject().get());

                    return false;
                });

                while (active) {
                    try {
                        execute(scanner.nextLine());
                    } catch (Exception e) {
                        active = false;
                    }
                }
            }
        }, "Lance-Console-Client").start();
    }
}
