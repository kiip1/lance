package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientSetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerSetPacket;

public class ServerSetListener extends ServerListener<ClientSetPacket> {
    public ServerSetListener() {
        super(new ClientSetPacket());
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientSetPacket packet) {
        String key = packet.key;
        byte[] data = packet.data;

        try {
            ServerSetPacket serverSetPacket = new ServerSetPacket();
            serverSetPacket.success = handler.server.storages[handler.storage].set(key, data);
            handler.fire(serverSetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
