package nl.kiipdevelopment.lance.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.listener.ServerListener;

public class ListServerListener extends ServerListener {
	public ListServerListener() {
		super((byte) -1);
	}

	@Override
	public void execute(ServerConnectionHandler handler, ClientPacket packet) {
//		handler.server.storage.list()
	}
}
