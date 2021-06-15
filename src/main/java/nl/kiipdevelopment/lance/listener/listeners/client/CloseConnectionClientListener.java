package nl.kiipdevelopment.lance.listener.listeners.client;

import nl.kiipdevelopment.lance.listener.ClientListener;
import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerCloseConnectionPacket;

public class CloseConnectionClientListener extends ClientListener {
    public CloseConnectionClientListener() {
        super((byte) 2);
    }

    @Override
    public void execute(ClientConnectionHandler handler, ServerPacket packet) {
        ServerCloseConnectionPacket closeConnectionPacket = (ServerCloseConnectionPacket) packet;

        handler.close(closeConnectionPacket.message);
    }
}
