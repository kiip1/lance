package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;

public class ServerHeartbeatPacket extends ServerPacket {
	public ServerHeartbeatPacket() {
		super((byte) 1, ServerHeartbeatPacket::new);
	}

	@Override
	public void read(DataInputStream reader) {}
}
