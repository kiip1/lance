package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.*;

public class ClientHandshakePacket extends ClientPacket {
	public String name;

	public ClientHandshakePacket() {
		super((byte) 0, ClientHandshakePacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		name = reader.readUTF();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeUTF(name);
	}
}
