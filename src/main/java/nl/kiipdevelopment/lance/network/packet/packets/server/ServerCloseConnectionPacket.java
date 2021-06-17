package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerCloseConnectionPacket extends ServerPacket {
	public String message;

	public ServerCloseConnectionPacket() {
		super((byte) 3, ServerCloseConnectionPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		message = reader.readUTF();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeUTF(message);
	}
}
