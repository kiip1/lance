package nl.kiipdevelopment.lance.network.listener.listeners.client;

import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ClientListener;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerCloseConnectionPacket;

public class ClientCloseConnectionListener extends ClientListener<ServerCloseConnectionPacket> {
    public ClientCloseConnectionListener() {
        super(new ServerCloseConnectionPacket());
    }

    @Override
    public void execute(ClientConnectionHandler handler, ServerCloseConnectionPacket packet) {
        handler.close(packet.message);
    }
}
