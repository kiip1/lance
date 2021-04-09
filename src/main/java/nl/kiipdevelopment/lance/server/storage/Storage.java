package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.concurrent.Executors;

public interface Storage<V> extends AutoCloseable {
	V get(String key) throws Exception;

	void set(String key, V value) throws Exception;
	
	boolean exists(String key) throws Exception;

	JsonArray list() throws Exception;

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

	default boolean isJson() {
		return false;
	}
}

