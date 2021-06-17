package nl.kiipdevelopment.lance.network.packet;

import java.util.function.Supplier;

public abstract class ClientPacket extends Packet {
	public ClientPacket(byte id, Supplier<Packet> supplier) {
		super(id, supplier);
	}
}