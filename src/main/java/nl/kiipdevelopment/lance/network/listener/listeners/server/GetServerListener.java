package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientGetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerGetPacket;
import nl.kiipdevelopment.lance.network.listener.ServerListener;

public class GetServerListener extends ServerListener {
    public GetServerListener() {
        super((byte) 6);
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientPacket packet) {
        ClientGetPacket clientGetPacket = (ClientGetPacket) packet;
        String key = clientGetPacket.key;

        try {
            ServerGetPacket serverGetPacket = new ServerGetPacket();
            serverGetPacket.data = handler.server.storages[handler.storage].get(key);
            handler.fire(serverGetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
