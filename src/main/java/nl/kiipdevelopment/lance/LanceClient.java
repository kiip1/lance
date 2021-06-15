package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.configuration.ClientConfiguration;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.listener.ClientListenerManager;
import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.PacketManager;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientGetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientSetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerGetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerSetPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class LanceClient extends Thread implements AutoCloseable {
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
        ClientListenerManager.init();

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

    /**
     * Retrieves a DataValue, being a json element or a string (file contents).
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path, separated by dots
     * @return The data at the path
     */
    public byte[] get(String key) {
        ClientGetPacket clientGetPacket = new ClientGetPacket();
        clientGetPacket.key = key;
        handler.fire(clientGetPacket);

        ServerGetPacket serverGetPacket = (ServerGetPacket) handler.next();

        return serverGetPacket.data;
    }
    
    /**
     * Same as LanceClient#get(String) but non-blocking, using a future.
     *
     * @param key The path, separated by dots
     * @return A future for the data at the path
     *
     * @see LanceClient#get(String)
     */
    public CompletableFuture<byte[]> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> get(key));
    }

    /**
     * Checks if a key exists. Works on both json and file based storage.
     * If something goes wrong in the process (shouldn't happen), the error handler is invoked.
     *
     * @param key The path, separated by dots
     * @return Whether the key exists or not
     */
    public boolean exists(String key) {
        throw new UnsupportedOperationException();
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
     * Lists the files in the storage directory. Be aware that if the server
     * does have a json based storage instead of files, this will give an error.
     *
     * @return The list of filenames
     */
    public String[] list() {
        throw new UnsupportedOperationException();
    }

    /**
     * Same as #listFilenames() but non-blocking, using a future.
     *
     * @return A future for the list of filenames
     */
    public CompletableFuture<String[]> listAsync() {
        return CompletableFuture.supplyAsync(this::list);
    }

    public boolean set(String key, byte[] value) {
        ClientSetPacket clientSetPacket = new ClientSetPacket();
        clientSetPacket.key = key;
        clientSetPacket.data = value;
        handler.fire(clientSetPacket);

        ServerSetPacket serverSetPacket = (ServerSetPacket) handler.next();

        return serverSetPacket.success;
    }

    public CompletableFuture<Boolean> setAsync(String key, byte[] value) {
        return CompletableFuture.supplyAsync(() -> set(key, value));
    }
    
    @Override
    public void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
