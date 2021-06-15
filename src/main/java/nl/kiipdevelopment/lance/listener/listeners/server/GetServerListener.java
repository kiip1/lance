package nl.kiipdevelopment.lance.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientGetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerGetPacket;
import nl.kiipdevelopment.lance.listener.ServerListener;

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
            serverGetPacket.data = handler.server.storage.get(key);
            handler.fire(serverGetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
