package nl.kiipdevelopment.lance.network.listener.listeners.client;

import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ClientListener;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerHeartbeatPacket;

public class ClientHeartbeatListener extends ClientListener<ServerHeartbeatPacket> {
    public ClientHeartbeatListener() {
        super(new ServerHeartbeatPacket());
    }

    @Override
    public void execute(ClientConnectionHandler handler, ServerHeartbeatPacket packet) {
        handler.missedHeartbeats = 0;
    }
}
