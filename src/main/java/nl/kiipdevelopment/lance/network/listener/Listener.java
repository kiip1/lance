package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.connection.ConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.Packet;

public abstract class Listener<T extends Packet> {
	public final byte id;

	public Listener(byte id) {
		this.id = id;
	}

	public abstract void execute(ConnectionHandler handler, T packet);
}
