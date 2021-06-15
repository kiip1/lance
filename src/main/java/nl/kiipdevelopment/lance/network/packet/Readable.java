package nl.kiipdevelopment.lance.network.packet;

import java.io.IOException;
import java.io.DataInputStream;

public interface Readable {
	void read(DataInputStream reader) throws IOException;
}
