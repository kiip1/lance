package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.DataInputStream;

public class ClientHeartbeatPacket extends ClientPacket {
	public ClientHeartbeatPacket() {
		super((byte) 1, ClientHeartbeatPacket::new);
	}

	@Override
	public void read(DataInputStream reader) {}
}
