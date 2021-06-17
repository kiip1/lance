package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientSwitchingStoragePacket;

public class ServerSwitchingStorageListener extends ServerListener<ClientSwitchingStoragePacket> {
    public ServerSwitchingStorageListener() {
        super(new ClientSwitchingStoragePacket());
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientSwitchingStoragePacket packet) {
        handler.storage = packet.storage;
    }
}
