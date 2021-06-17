package nl.kiipdevelopment.lance.network.packet;

import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public abstract class Packet implements Readable, Writeable {
	public final byte id;
	public final Supplier<Packet> supplier;

	public Packet(byte id, Supplier<Packet> supplier) {
		this.id = id;
		this.supplier = supplier;
	}

	@MustBeInvokedByOverriders
	@Override
	public void write(DataOutputStream writer) throws IOException {
		writer.write(id);
	}
}
