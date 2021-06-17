package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientExistsPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerExistsPacket;

public class ServerExistsListener extends ServerListener<ClientExistsPacket> {
    public ServerExistsListener() {
        super(new ClientExistsPacket());
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientExistsPacket packet) {
        String key = packet.key;

        try {
            ServerExistsPacket serverExistsPacket = new ServerExistsPacket();
            serverExistsPacket.exists = handler.server.storages[handler.storage].exists(key);
            handler.fire(serverExistsPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
