package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientCloseConnectionPacket;

public class CloseConnectionServerListener extends ServerListener {
    public CloseConnectionServerListener() {
        super((byte) 5);
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientPacket packet) {
        ClientCloseConnectionPacket closeConnectionPacket = (ClientCloseConnectionPacket) packet;

        handler.close(closeConnectionPacket.message);
    }
}
