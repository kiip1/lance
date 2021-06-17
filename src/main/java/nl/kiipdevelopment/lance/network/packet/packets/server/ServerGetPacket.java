package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerGetPacket extends ServerPacket {
	public byte[] data;

	public ServerGetPacket() {
		super((byte) 5, ServerGetPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		int length = reader.readInt();
		data = reader.readNBytes(length);
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		int length = data.length;
		writer.writeInt(length);
		writer.write(data, 0, length);
	}
}
