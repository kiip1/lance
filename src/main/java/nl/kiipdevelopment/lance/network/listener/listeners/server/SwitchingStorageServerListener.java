package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientSwitchingStoragePacket;

public class SwitchingStorageServerListener extends ServerListener {
    public SwitchingStorageServerListener() {
        super((byte) 10);
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientPacket packet) {
        ClientSwitchingStoragePacket switchingStoragePacket = (ClientSwitchingStoragePacket) packet;

        handler.storage = switchingStoragePacket.storage;
    }
}
