package nl.kiipdevelopment.lance.network.packet;

import java.util.function.Supplier;

public abstract class Packet implements Readable, Writeable {
	public final byte id;
	public final Supplier<Packet> supplier;

	public Packet(byte id, Supplier<Packet> supplier) {
		this.id = id;
		this.supplier = supplier;
	}
}
