package nl.kiipdevelopment.lance.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.configuration.Configuration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;

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
        super(host, port, configuration, "Lance-Console-Client");

        init();
    }

    private void init() {
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (socket == null || out == null || in == null || listenerManager == null)
                    Thread.onSpinWait();

                System.out.println("[" + getName() + "] " + "Console client started on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort() + ".");

                listenerManager.listen(lanceMessage -> {
                    if (lanceMessage == null) out.close();
                    else System.out.println(
                        "[" + getName() + "] " +
                        lanceMessage.getMessage() +
                        (lanceMessage.getJson() == null ? "" : " $ " + lanceMessage.getJson())
                    );

                    return false;
                });

                while (true) {
                    try {
                        String line = scanner.nextLine();
                        String[] parts = line.split("(?<!\\\\)\\$");

                        if (parts.length == 2) {
                            JsonElement element = new Gson().fromJson(parts[1].replace("\\$", "$"), JsonElement.class);

                            if (element.isJsonObject())
                                execute(
                                    parts[0].replace("\\$", "$"),
                                    element.getAsJsonObject()
                                );
                            else
                                execute(
                                    parts[0].replace("\\$", "$"),
                                    element
                                );
                        } else execute(line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "Lance-Console-Client").start();
    }
}
