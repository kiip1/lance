package nl.kiipdevelopment.lance.network.packet;

import java.io.IOException;
import java.io.DataOutputStream;

public interface Writeable {
	void write(DataOutputStream writer) throws IOException;
}
