package nl.kiipdevelopment.lance.server.storage;

import java.io.IOException;
import java.nio.file.Path;

final class StorageHelper {
    private static StorageType defaultStorageType;
    private static Path defaultStorageLocation;

    static void updateDefaultStorage(StorageType type, Path location) {
        defaultStorageType = type;
        defaultStorageLocation = location;
    }

    static <T> Storage<T> getDefaultStorage() {
        return getStorage(defaultStorageType, defaultStorageLocation);
    }

    static <T> Storage<T> getStorage(StorageType type) {
        return getStorage(type, type.getDefaultLocation());
    }

    static <T> Storage<T> getStorage(StorageType type, Path location) {
        try {
            return (Storage<T>) type.getStorage(location);
        } catch (IOException | StorageException e) {
            e.printStackTrace();
        }

        return null;
    }
}
