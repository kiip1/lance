package nl.kiipdevelopment.lance.network.listener.listeners.server;

import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.ServerListener;
import nl.kiipdevelopment.lance.network.packet.packets.client.ClientListPacket;
import nl.kiipdevelopment.lance.network.packet.packets.server.ServerListPacket;

public class ServerListListener extends ServerListener<ClientListPacket> {
	public ServerListListener() {
		super(new ClientListPacket());
	}

	@Override
	public void execute(ServerConnectionHandler handler, ClientListPacket packet) {
		String key = packet.key;

		try {
			ServerListPacket serverListPacket = new ServerListPacket();
			serverListPacket.keys = handler.server.storages[handler.storage].list(key);
			handler.fire(serverListPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
