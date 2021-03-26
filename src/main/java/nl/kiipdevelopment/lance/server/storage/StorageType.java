package nl.kiipdevelopment.lance.server.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public enum StorageType {
	JSON(0, JsonStorage::new, "database.json");
	
	private final int id;
	private final StorageSupplier supplier;
	private final String defaultLocation;
	
	StorageType(int id, StorageSupplier supplier, String defaultLocation) {
		this.id = id;
		this.supplier = supplier;
		this.defaultLocation = defaultLocation;
	}
	
	public Storage<?> getStorage(Path location) throws IOException, StorageException {
		return supplier.get(location);
	}
	
	public String getDefaultLocation() {
		return defaultLocation;
	}
	
	private static final Map<Integer, StorageType> BY_ID = new HashMap<>();
	
	static {
		for (StorageType type : values()) {
			BY_ID.put(type.id, type);
		}
	}
	
	public static StorageType getById(int id) {
		return BY_ID.get(id);
	}
	
	public static StorageType getDefault() {
		return JSON;
	}
	
	private interface StorageSupplier {
		Storage<?> get(Path location) throws IOException, StorageException;
	}
}
