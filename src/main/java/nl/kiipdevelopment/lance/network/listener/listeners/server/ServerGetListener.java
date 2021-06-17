package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientGetPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerGetPacket;

public class ServerGetListener extends ServerListener<ClientGetPacket> {
    public ServerGetListener() {
        super(new ClientGetPacket());
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientGetPacket packet) {
        String key = packet.key;

        try {
            ServerGetPacket serverGetPacket = new ServerGetPacket();
            serverGetPacket.data = handler.server.storages[handler.storage].get(key);
            handler.fire(serverGetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
