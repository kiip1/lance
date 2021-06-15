package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientCloseConnectionPacket extends ClientPacket {
	public String message;

	public ClientCloseConnectionPacket() {
		super((byte) 5, ClientCloseConnectionPacket::new);
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
