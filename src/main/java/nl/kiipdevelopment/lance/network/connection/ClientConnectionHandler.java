package nl.kiipdevelopment.lance.network.connection;

import nl.kiipdevelopment.lance.Validate;
import nl.kiipdevelopment.lance.configuration.ClientConfiguration;
import nl.kiipdevelopment.lance.network.listener.ClientListenerManager;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.PacketManager;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientCloseConnectionPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientHandshakePacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientHeartbeatPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientPasswordPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerHandshakePacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerWelcomePacket;
import org.jetbrains.annotations.Contract;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientConnectionHandler {
    public final ClientConfiguration configuration;
    public final DataInputStream reader;
    public final DataOutputStream writer;

    public boolean active = true;
    public byte storage;
    public byte missedHeartbeats;

    private final List<ServerPacket> queue = new ArrayList<>();

    public ClientConnectionHandler(ClientConfiguration configuration, DataInputStream reader, DataOutputStream writer) {
        this.configuration = configuration;
        this.reader = reader;
        this.writer = writer;

        run();
    }

    public void run() {
        boolean authorised = true;

        ClientHandshakePacket clientHandshakePacket = new ClientHandshakePacket();
        clientHandshakePacket.name = configuration.name;
        clientHandshakePacket.version = PacketManager.VERSION;
        fire(clientHandshakePacket);

        ServerHandshakePacket serverHandshakePacket = (ServerHandshakePacket) get();
        boolean authorisationRequired = serverHandshakePacket.authorisationRequired;

        if (authorisationRequired) {
            authorised = false;

            ClientPasswordPacket passwordPacket = new ClientPasswordPacket();
            passwordPacket.password = configuration.password;
            fire(passwordPacket);
        } else {
            Validate.ensure(get() instanceof ServerWelcomePacket, "Server didn't welcome.");
        }

        if (authorised) {
            Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    missedHeartbeats++;

                    ClientHeartbeatPacket heartbeatPacket = new ClientHeartbeatPacket();
                    fire(heartbeatPacket);

                    if (missedHeartbeats > 3) {
                        close("Timed out.");
                    }
                }, 0, 5, TimeUnit.SECONDS);

            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                while (active) {
                    ServerPacket packet = get();

                    if (active) {
                        ClientListenerManager.handle(this, packet);
                    }
                }
            });
        }
    }

    public ServerPacket next() {
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (queue.size() == 0) {
            Thread.onSpinWait();
        }

        return queue.remove(0);
    }

    @Contract("-> !null")
    private ServerPacket get() {
        try {
            byte id = reader.readByte();

            ServerPacket packet = (ServerPacket) PacketManager.obtain(id)
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

    public void fire(ClientPacket packet) {
        if (!active) {
            return;
        }

        try {
            packet.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void satisfy(ServerPacket packet) {
        queue.add(packet);
    }

    public void close(String message) {
        if (!active) {
            return;
        }

        ClientCloseConnectionPacket packet = new ClientCloseConnectionPacket();
        packet.message = message;
        fire(packet);

        active = false;
    }
}
