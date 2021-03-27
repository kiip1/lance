package nl.kiipdevelopment.lance.server.storage;

import java.nio.file.Path;

public interface Storage<V> extends AutoCloseable {
	V get(String key) throws Exception;

	void set(String key, V value) throws Exception;
	
	boolean exists(String key) throws Exception;
	
	default boolean isJson() {
		return false;
	}

	static void updateDefaultStorage(StorageType type, Path location) {
		StorageHelper.updateDefaultStorage(type, location);
	}

	static <T> Storage<T> getDefaultStorage() {
		return StorageHelper.getDefaultStorage();
	}

	static <T> Storage<T> getStorage(StorageType type) {
		return StorageHelper.getStorage(type);
	}

	static <T> Storage<T> getStorage(StorageType type, Path location) {
		return StorageHelper.getStorage(type, location);
	}
}

