package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientSetPacket extends ClientPacket {
	public String key;
	public byte[] data;

	public ClientSetPacket() {
		super((byte) 8, ClientSetPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		key = reader.readUTF();

		int length = reader.readInt();
		data = reader.readNBytes(length);
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeUTF(key);

		int length = data.length;
		writer.writeInt(length);
		writer.write(data, 0, length);
	}
}
