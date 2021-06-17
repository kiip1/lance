package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.configuration.ServerConfiguration;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListenerManager;
import nl.kiipdevelopment.lance.network.packet.PacketManager;
import nl.kiipdevelopment.lance.storage.Storage;
import nl.kiipdevelopment.lance.storage.StorageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class LanceServer extends Thread {
    private final ServerConfiguration configuration;
    private final String host;
    private final int port;

    public final List<ServerConnectionHandler> handlers = new ArrayList<>();

    private ServerSocket socket;

    public Storage[] storages = new Storage[StorageType.values().length];

    public LanceServer() {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, DefaultConfiguration.getServerConfiguration());
    }

    public LanceServer(ServerConfiguration configuration) {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, configuration);
    }

    public LanceServer(String host, int port) {
        this(host, port, DefaultConfiguration.getServerConfiguration());
    }

    public LanceServer(String host, int port, ServerConfiguration configuration) {
        super("Lance-Server");

        this.host = host;
        this.port = port;
        this.configuration = configuration;

        PacketManager.init();
        ServerListenerManager.init();

        start();
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < storages.length; i++) {
                storages[i] = configuration.storageType.getStorage(configuration.storageLocation);

                System.out.println("[" + getName() + "] " + "Adding storage " + configuration.storageType.name());
                System.out.println("[" + getName() + "] " + "Storage location is '" + configuration.storageLocation.toPath().toAbsolutePath() + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }

        try {
            socket = new ServerSocket(port, configuration.backlog, InetAddress.getByName(host));

            System.out.println("[" + getName() + "] " + "Server started on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());

            while (!socket.isClosed())
                try {
                    ServerConnectionHandler handler = new ServerConnectionHandler(this, configuration, socket.accept());
                    handlers.add(handler);
                } catch (IOException e) {
                    if (e.getMessage().equals("Socket closed")) {
                        System.out.println("[" + getName() + "] " + "Server shut down.");
                    } else {
                        e.printStackTrace();
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            for (ServerConnectionHandler handler : handlers) {
                handler.close("Server stopping.");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
