package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.*;

public class ClientPasswordPacket extends ClientPacket {
	public String password;

	public ClientPasswordPacket() {
		super((byte) 2, ClientPasswordPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		password = reader.readUTF();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeUTF(password);
	}
}
