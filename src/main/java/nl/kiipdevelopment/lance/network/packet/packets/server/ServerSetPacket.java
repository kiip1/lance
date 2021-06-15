package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerSetPacket extends ServerPacket {
	public boolean success;

	public ServerSetPacket() {
		super((byte) 9, ServerSetPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		success = reader.readBoolean();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeBoolean(success);
	}
}
