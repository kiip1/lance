package nl.kiipdevelopment.lance.network.packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public abstract class ClientPacket extends Packet {
	public ClientPacket(byte id, Supplier<Packet> supplier) {
		super(id, supplier);
	}

	@Override
	public void write(DataOutputStream writer) throws IOException {
		writer.write(id);
	}
}