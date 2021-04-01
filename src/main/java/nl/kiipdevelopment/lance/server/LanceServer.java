package nl.kiipdevelopment.lance.server;

import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.configuration.ServerConfiguration;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.command.CommandManager;
import nl.kiipdevelopment.lance.server.storage.Storage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class LanceServer extends Thread {
    private final ServerConfiguration configuration;
    private final String host;
    private final int port;

    private ServerSocket serverSocket;
    public Storage<?> storage;

    public final List<ServerConnectionHandler> handlers = new ArrayList<>();
    public final List<String> connections = new ArrayList<>();
    public boolean active = true;

    public LanceServer() {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, DefaultConfiguration.getDefaultServerConfiguration());
    }

    public LanceServer(ServerConfiguration configuration) {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, configuration);
    }

    public LanceServer(String host, int port) {
        this(host, port, DefaultConfiguration.getDefaultServerConfiguration());
    }

    public LanceServer(String host, int port, ServerConfiguration configuration) {
        super("Lance-Server");

        this.host = host;
        this.port = port;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        try {
            storage = configuration.getStorageType().getStorage(configuration.getStorageLocation());

            System.out.println("[" + getName() + "] " + "Using storage " + configuration.getStorageType().name());
            System.out.println("[" + getName() + "] " + "Storage location is '" + configuration.getStorageLocation().toPath().toAbsolutePath() + "'");
        } catch (IOException e) {
            System.err.println("Error happened while initializing storage");
            e.printStackTrace();
            return;
        }
        
        CommandManager.init();

        try {
            serverSocket = new ServerSocket(port, configuration.getBacklog(), InetAddress.getByName(host));

            System.out.println("[" + getName() + "] " + "Server started on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

            while (active)
                try {
                    ServerConnectionHandler handler = new ServerConnectionHandler(this, configuration, serverSocket.accept());
                    handler.start();

                    handlers.add(handler);
                } catch (SocketException e) {
                    if (e.getMessage().equals("Socket closed"))
                        System.out.println("[" + getName() + "] " + "Server shut down.");
                    else e.printStackTrace();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        active = false;

        try {
            serverSocket.close();

            for (ServerConnectionHandler handler : handlers)
                handler.close(StatusCode.CLOSING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
