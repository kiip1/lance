package nl.kiipdevelopment.lance.server.storage;

import java.io.IOException;
import java.nio.file.Path;

public enum StorageType {
	JSON(0, JsonStorage::new, Path.of("database.json")),
	FILE(1, FileStorage::new, Path.of("files"));

	private final int id;
	private final StorageSupplier supplier;
	private final Path defaultLocation;
	
	StorageType(int id, StorageSupplier supplier, Path defaultLocation) {
		this.id = id;
		this.supplier = supplier;
		this.defaultLocation = defaultLocation;
	}
	
	public Storage<?> getStorage(Path location) throws IOException, StorageException {
		return supplier.get(location);
	}
	
	public Path getDefaultLocation() {
		return defaultLocation;
	}
	
	public static StorageType getById(int id) {
		for (StorageType type : values())
			if (type.id == id)
				return type;

		return null;
	}
	
	public static StorageType getDefault() {
		return JSON;
	}

	private interface StorageSupplier {
		Storage<?> get(Path location) throws IOException, StorageException;
	}
}
