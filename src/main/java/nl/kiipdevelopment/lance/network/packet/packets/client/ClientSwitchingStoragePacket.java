package nl.kiipdevelopment.lance.network.packet.packets.client;

import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientSwitchingStoragePacket extends ClientPacket {
	public byte storage;

	public ClientSwitchingStoragePacket() {
		super((byte) 3, ClientSwitchingStoragePacket::new);
	}

	@Override
	public void read(DataInputStream reader) throws IOException {
		storage = reader.readByte();
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		super.write(writer);

		writer.writeByte(storage);
	}
}
