package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.listener.ServerListener;

public class StopServerListener extends ServerListener {
    public StopServerListener() {
        super((byte) -1);
    }

    @Override
    public void execute(ServerConnectionHandler handler, ClientPacket packet) {

    }
}
