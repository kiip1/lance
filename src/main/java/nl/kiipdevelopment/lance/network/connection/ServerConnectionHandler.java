package nl.kiipdevelopment.lance.network.connection;

import nl.kiipdevelopment.lance.LanceServer;
import nl.kiipdevelopment.lance.Validate;
import nl.kiipdevelopment.lance.configuration.ServerConfiguration;
import nl.kiipdevelopment.lance.network.listener.ListenerManager;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.PacketManager;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientHandshakePacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientPasswordPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerCloseConnectionPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerHandshakePacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerHeartbeatPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerWelcomePacket;
import org.jetbrains.annotations.Contract;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerConnectionHandler implements ConnectionHandler {
    public final LanceServer server;
    public final ServerConfiguration configuration;
    public final Socket socket;

    public DataInputStream reader;
    public DataOutputStream writer;
    public boolean active = true;
    public byte storage;
    public byte missedHeartbeats;
    public String name = "Client";

    public ServerConnectionHandler(LanceServer server, ServerConfiguration configuration, Socket socket) {
        this.server = server;
        this.configuration = configuration;
        this.socket = socket;

        try {
            socket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        run();
    }

    public void run() {
        try {
            String ipAndPort = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    
            System.out.println("[Lance-Server-Connection-Handler] Accepted connection from " + ipAndPort + ".");

            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            boolean authorisationRequired = configuration.passwordEnabled;
            boolean authorised = true;

            ClientHandshakePacket clientHandShakePacket = (ClientHandshakePacket) next();
            name = clientHandShakePacket.name;
            short version = clientHandShakePacket.version;

            if (version == PacketManager.version) {
                ServerHandshakePacket serverHandshakePacket = new ServerHandshakePacket();
                serverHandshakePacket.authorisationRequired = authorisationRequired;
                fire(serverHandshakePacket);

                if (authorisationRequired) {
                    authorised = false;

                    ClientPasswordPacket passwordPacket = (ClientPasswordPacket) next();
                    String password = passwordPacket.password;

                    if (password.equals(configuration.password)) {
                        authorised = true;
                    } else {
                        close("Incorrect password.");
                    }
                }

                if (authorised) {
                    ServerWelcomePacket welcomePacket = new ServerWelcomePacket();
                    fire(welcomePacket);

                    Executors.newSingleThreadScheduledExecutor()
                        .scheduleAtFixedRate(() -> {
                            missedHeartbeats++;

                            ServerHeartbeatPacket heartbeatPacket = new ServerHeartbeatPacket();
                            fire(heartbeatPacket);

                            if (missedHeartbeats > 3) {
                                close("Timed out.");
                            }
                        }, 0, 5, TimeUnit.SECONDS);

                    Executors.newSingleThreadScheduledExecutor().execute(() -> {
                        while (active) {
                            ClientPacket packet = next();

                            if (active) {
                                ListenerManager.handle(this, packet);
                            }
                        }

                        System.out.println("[Lance-Server-Connection-Handler] Closed connection from " + ipAndPort + ".");
                    });
                }
            } else {
                close("Version is different.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Contract("-> !null")
    public ClientPacket next() {
        try {
            byte id = reader.readByte();

            ClientPacket packet = (ClientPacket) PacketManager.obtain(id)
                .get();

            packet.read(reader);

            return packet;
        } catch (IOException e) {
            if (!e.getMessage().equals("Socket closed")) {
                e.printStackTrace();

                close("Bad packet.");
            }
        }

        Validate.fail("Bad packet.");

        return null;
    }

    public void fire(ServerPacket packet) {
        if (!active) {
            return;
        }

        try {
            packet.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(String message) {
        if (!active) {
            return;
        }

        ServerCloseConnectionPacket packet = new ServerCloseConnectionPacket();
        packet.message = message;
        fire(packet);

        delete();

        System.out.println("[Lance-Server-Connection-Handler] " + name + " disconnected with the reason " + message);
    }

    public void delete() {
        server.handlers.remove(this);

        active = false;
    }
}
