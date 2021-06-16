package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientListPacket extends ClientPacket {
	public String key;

	public ClientListPacket() {
		super((byte) 12, ClientListPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		key = reader.readUTF();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeUTF(key);
	}
}
