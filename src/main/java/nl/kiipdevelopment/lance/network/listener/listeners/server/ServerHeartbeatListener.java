package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientHeartbeatPacket;

public class ServerHeartbeatListener extends ServerListener<ClientHeartbeatPacket> {
    public ServerHeartbeatListener() {
        super(new ClientHeartbeatPacket());
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientHeartbeatPacket packet) {
        handler.missedHeartbeats = 0;
    }
}
