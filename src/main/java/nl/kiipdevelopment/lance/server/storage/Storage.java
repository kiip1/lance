package nl.kiipdevelopment.lance.server.storage;

import java.nio.file.Path;

public interface Storage<V> extends AutoCloseable {
	V get(String key);

	void set(String key, V value);
	
	boolean exists(String key);

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

