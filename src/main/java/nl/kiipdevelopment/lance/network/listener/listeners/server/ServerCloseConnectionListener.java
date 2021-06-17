package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientCloseConnectionPacket;

public class ServerCloseConnectionListener extends ServerListener<ClientCloseConnectionPacket> {
    public ServerCloseConnectionListener() {
        super(new ClientCloseConnectionPacket());
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientCloseConnectionPacket packet) {
        handler.delete();
    }
}
