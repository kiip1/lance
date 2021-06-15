package nl.kiipdevelopment.lance.network.packet;

import java.io.IOException;
import java.io.DataOutputStream;
import java.util.function.Supplier;

public abstract class ServerPacket extends Packet {
	public ServerPacket(byte id, Supplier<Packet> supplier) {
		super(id, supplier);
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		writer.write(id);
	}
}
