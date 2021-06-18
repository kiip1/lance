package nl.kiipdevelopment.lance;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.configuration.ClientConfiguration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ListenerManager;
import nl.kiipdevelopment.lance.network.packet.PacketManager;
import nl.kiipdevelopment.lance.network.packet.packets.client.*;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerExistsPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerGetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerListPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerSetPacket;
import nl.kiipdevelopment.lance.storage.StorageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LanceClient extends Thread implements AutoCloseable {
    private static final Gson gson = new Gson();

    private final ClientConfiguration configuration;
    private final String host;
    private final int port;

    public Socket socket;
    public DataInputStream reader;
    public DataOutputStream writer;
    public volatile ClientConnectionHandler handler;

    private int retries = 0;

    public LanceClient() {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, DefaultConfiguration.getClientConfiguration());
    }

    public LanceClient(ClientConfiguration configuration) {
        this(DefaultConfiguration.HOST, DefaultConfiguration.PORT, configuration);
    }

    public LanceClient(String host, int port) {
        this(host, port, DefaultConfiguration.getClientConfiguration());
    }

    public LanceClient(String host, int port, ClientConfiguration configuration) {
        super("Lance-Client");

        this.host = host;
        this.port = port;
        this.configuration = configuration;

        PacketManager.init();
        ListenerManager.initClient();

        start();

        while (handler == null) {
            Thread.onSpinWait();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
            handler = new ClientConnectionHandler(configuration, reader, writer);
        } catch (IOException e) {
            if (retries++ < configuration.maxRetries || configuration.maxRetries == -1) {
                try {
                    Thread.sleep(configuration.retryTimeout);

                    System.out.println(
                        "[" + getName() + "] " +
                            e.getMessage() + " | Retrying..." +
                            (configuration.maxRetries == -1
                                ? ""
                                : "(" + retries + "/" + configuration.maxRetries + ")"
                            )
                    );

                    run();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        }
    }

    private byte[] get(String key) {
        ClientGetPacket clientGetPacket = new ClientGetPacket();
        clientGetPacket.key = key;
        handler.fire(clientGetPacket);

        ServerGetPacket serverGetPacket = (ServerGetPacket) handler.next();

        return serverGetPacket.data;
    }

    private boolean set(String key, byte[] value) {
        ClientSetPacket clientSetPacket = new ClientSetPacket();
        clientSetPacket.key = key;
        clientSetPacket.data = value;
        handler.fire(clientSetPacket);

        ServerSetPacket serverSetPacket = (ServerSetPacket) handler.next();

        return serverSetPacket.success;
    }

    private boolean exists(String key) {
        ClientExistsPacket clientExistsPacket = new ClientExistsPacket();
        clientExistsPacket.key = key;
        handler.fire(clientExistsPacket);

        ServerExistsPacket serverExistsPacket = (ServerExistsPacket) handler.next();

        return serverExistsPacket.exists;
    }

    private String[] list(String key) {
        ClientListPacket clientListPacket = new ClientListPacket();
        clientListPacket.key = key;
        handler.fire(clientListPacket);

        ServerListPacket serverListPacket = (ServerListPacket) handler.next();

        return serverListPacket.keys;
    }

    public JsonElement getJson(String key) {
        switchStorage(StorageType.JSON.id);

        return gson.fromJson(new String(get(key)), JsonElement.class);
    }

    public boolean setJson(String key, JsonElement value) {
        switchStorage(StorageType.JSON.id);

        return set(key, gson.toJson(value).getBytes());
    }

    public boolean existsJson(String key) {
        switchStorage(StorageType.JSON.id);

        return exists(key);
    }

    public String[] listJson(String key) {
        switchStorage(StorageType.JSON.id);

        return list(key);
    }

    public byte[] getFile(String key) {
        switchStorage(StorageType.FILE.id);

        return get(key);
    }

    public boolean setFile(String key, byte[] value) {
        switchStorage(StorageType.FILE.id);

        return set(key, value);
    }

    public boolean existsFile(String key) {
        switchStorage(StorageType.FILE.id);

        return exists(key);
    }

    public String[] listFile(String key) {
        switchStorage(StorageType.FILE.id);

        return list(key);
    }

    private void switchStorage(byte id) {
        if (handler.storage != id) {
            ClientSwitchingStoragePacket switchingStoragePacket = new ClientSwitchingStoragePacket();
            switchingStoragePacket.storage = id;
            handler.fire(switchingStoragePacket);
        }
    }

    @Override
    public void close() {
        handler.close("Disconnected by user.");

        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
