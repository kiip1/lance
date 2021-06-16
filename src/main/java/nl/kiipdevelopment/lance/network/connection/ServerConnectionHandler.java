package nl.kiipdevelopment.lance.network.connection;

import nl.kiipdevelopment.lance.LanceServer;
import nl.kiipdevelopment.lance.configuration.ServerConfiguration;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.PacketManager;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientHandshakePacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientPasswordPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerCloseConnectionPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerHandshakePacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerWelcomePacket;
import nl.kiipdevelopment.lance.network.listener.ServerListenerManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerConnectionHandler {
    public final LanceServer server;
    public final ServerConfiguration configuration;
    public final Socket socket;

    public DataInputStream reader;
    public DataOutputStream writer;
    public boolean active = true;
    public byte storage;
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

            if (version == PacketManager.VERSION) {
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

                    while (active) {
                        ClientPacket packet = next();

                        if (active) {
                            ServerListenerManager.handle(this, packet);
                        }
                    }
                }
            } else {
                close("Version is different.");
            }
    
            System.out.println("[Lance-Server-Connection-Handler] Closed connection from " + ipAndPort + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientPacket next() {
        try {
            byte id = reader.readByte();

            ClientPacket packet = (ClientPacket) PacketManager.obtain(id)
                .get();

            packet.read(reader);

            return packet;
        } catch (IOException e) {
            e.printStackTrace();

            close("Bad packet.");
        }

        return null;
    }

    public void fire(ServerPacket packet) {
        try {
            packet.write(writer);
        } catch (IOException e) {
            e.printStackTrace();

            close("Bad packet.");
        }
    }

    public void close(String message) {
        active = false;

        System.out.println("[Lance-Server-Connection-Handler] " + name + " disconnected with the reason " + message);

        ServerCloseConnectionPacket packet = new ServerCloseConnectionPacket();

        packet.message = message;

        fire(packet);
    }
}
