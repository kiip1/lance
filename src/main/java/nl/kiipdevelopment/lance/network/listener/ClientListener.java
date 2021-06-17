package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;

public abstract class ClientListener<T extends ServerPacket> {
    public final byte id;

    public ClientListener(T packet) {
        this.id = packet.id;
    }

    public abstract void execute(ClientConnectionHandler handler, T packet);
}
