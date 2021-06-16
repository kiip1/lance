package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerExistsPacket extends ServerPacket {
	public boolean exists;

	public ServerExistsPacket() {
		super((byte) 14, ServerExistsPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		exists = reader.readBoolean();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeBoolean(exists);
	}
}
