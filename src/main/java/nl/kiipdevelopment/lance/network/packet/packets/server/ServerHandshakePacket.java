package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerHandshakePacket extends ServerPacket {
	public boolean authorisationRequired;

	public ServerHandshakePacket() {
		super((byte) 1, ServerHandshakePacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		authorisationRequired = reader.readBoolean();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeBoolean(authorisationRequired);
	}
}
