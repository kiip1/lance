package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientSetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerSetPacket;

public class SetServerListener extends ServerListener {
    public SetServerListener() {
        super((byte) 8);
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientPacket packet) {
        ClientSetPacket clientSetPacket = (ClientSetPacket) packet;
        String key = clientSetPacket.key;
        byte[] data = clientSetPacket.data;

        try {
            ServerSetPacket serverSetPacket = new ServerSetPacket();
            serverSetPacket.success = handler.server.storages[handler.storage].set(key, data);
            handler.fire(serverSetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
