package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;

public class ServerWelcomePacket extends ServerPacket {
	public ServerWelcomePacket() {
		super((byte) 4, ServerWelcomePacket::new);
	}

	@Override
	public void read(DataInputStream reader) {}
}
