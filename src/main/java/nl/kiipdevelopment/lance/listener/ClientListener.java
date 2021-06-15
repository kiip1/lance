package nl.kiipdevelopment.lance.listener;

import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;

public abstract class ClientListener {
    public final byte id;

    public ClientListener(byte id) {
        this.id = id;
    }

    public abstract void execute(ClientConnectionHandler handler, ServerPacket packet);
}
