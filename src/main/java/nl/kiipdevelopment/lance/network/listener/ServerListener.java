package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;

public abstract class ServerListener<T extends ClientPacket> {
    public final byte id;

    public ServerListener(T packet) {
        this.id = packet.id;
    }

    public abstract void execute(ServerConnectionHandler handler, T packet);
}
