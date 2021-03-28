package nl.kiipdevelopment.lance.server.storage;

import nl.kiipdevelopment.lance.server.LanceServer;

import java.io.IOException;
import java.util.concurrent.Executors;

public interface Storage<V> extends AutoCloseable {
	V get(String key) throws Exception;

	void set(String key, V value) throws Exception;
	
	boolean exists(String key) throws Exception;
	
	default boolean isJson() {
		return false;
	}
	
	void save() throws IOException;
	
	@Override
	default void close() throws Exception {
		save();
	}
	
	default void initAutoSave(int interval, LanceServer server) {
		Executors.newSingleThreadExecutor().submit(() -> {
			long intervalMillis = interval * 1000L;
			long lastAutosaveTime = System.currentTimeMillis();
			
			while (server.active) {
				while (System.currentTimeMillis() - lastAutosaveTime < intervalMillis && server.active)
					Thread.onSpinWait();
				
				try {
					save();
					lastAutosaveTime = System.currentTimeMillis();
				} catch (IOException e) {
					System.out.println("[" + Thread.currentThread().getName() + "] " + "Unable to save storage: " + e);
				}
			}
		});
	}
}

