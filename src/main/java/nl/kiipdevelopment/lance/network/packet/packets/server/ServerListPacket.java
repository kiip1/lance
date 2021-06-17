package nl.kiipdevelopment.lance.network.packet.packets.server;

import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerListPacket extends ServerPacket {
	public String[] keys = new String[0];

	public ServerListPacket() {
		super((byte) 6, ServerListPacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		int length = reader.readInt();

		List<String> keyList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			keyList.add(reader.readUTF());
		}

		keys = keyList.toArray(String[]::new);
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		int length = keys.length;
		writer.writeInt(length);

		for (String key : keys) {
			writer.writeUTF(key);
		}
	}
}
