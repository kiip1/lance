package nl.kiipdevelopment.lance.listener;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;

public abstract class ServerListener {
    public final byte id;

    public ServerListener(byte id) {
        this.id = id;
    }

    public abstract void execute(ServerConnectionHandler handler, ClientPacket packet);
}
