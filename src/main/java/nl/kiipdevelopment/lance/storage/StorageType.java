package nl.kiipdevelopment.lance.storage;

import java.io.File;
import java.io.IOException;

public enum StorageType {
	JSON(0, JsonStorage::new, new File("database.json")),
	FILE(1, FileStorage::new, new File("files"));

	private final int id;
	private final StorageSupplier supplier;
	private final File defaultLocation;
	
	StorageType(int id, StorageSupplier supplier, File defaultLocation) {
		this.id = id;
		this.supplier = supplier;
		this.defaultLocation = defaultLocation;
	}
	
	public Storage getStorage(File location) throws IOException {
		return supplier.get(location);
	}
	
	public File getDefaultLocation() {
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
		Storage get(File location) throws IOException;
	}
}
