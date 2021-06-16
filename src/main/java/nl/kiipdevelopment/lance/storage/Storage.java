package nl.kiipdevelopment.lance.storage;

import java.io.IOException;
import java.util.concurrent.Executors;

public interface Storage extends AutoCloseable {
	byte[] get(String key) throws Exception;

	boolean set(String key, byte[] value) throws Exception;
	
	boolean exists(String key) throws Exception;

	String[] list(String key) throws Exception;

	void save() throws IOException;

	@Override
	default void close() {
		Executors.newSingleThreadExecutor().submit(() -> {
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}

