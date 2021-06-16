package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientExistsPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerExistsPacket;

public class ExistsServerListener extends ServerListener {
    public ExistsServerListener() {
        super((byte) 11);
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientPacket packet) {
        ClientExistsPacket clientExistsPacket = (ClientExistsPacket) packet;
        String key = clientExistsPacket.key;

        try {
            ServerExistsPacket serverExistsPacket = new ServerExistsPacket();
            serverExistsPacket.exists = handler.server.storages[handler.storage].exists(key);
            handler.fire(serverExistsPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
