package nl.kiipdevelopment.lance.network.packet;

import java.util.function.Supplier;

public abstract class ServerPacket extends Packet {
	public ServerPacket(byte id, Supplier<Packet> supplier) {
		super((byte) (id - 128), supplier);
	}
}
