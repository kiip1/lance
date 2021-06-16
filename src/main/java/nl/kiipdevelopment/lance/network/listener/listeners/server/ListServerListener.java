package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientListPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerListPacket;

public class ListServerListener extends ServerListener {
	public ListServerListener() {
		super((byte) 12);
	}

	@Override
	public void execute(ServerConnectionHandler handler, ClientPacket packet) {
		ClientListPacket clientListPacket = (ClientListPacket) packet;
		String key = clientListPacket.key;

		try {
			ServerListPacket serverListPacket = new ServerListPacket();
			serverListPacket.keys = handler.server.storages[handler.storage].list(key);
			handler.fire(serverListPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
