package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.server.storage.StorageType;

import java.io.File;
import java.nio.file.Path;

public class ServerConfiguration extends Configuration {
    private final int backlog;
    private final StorageType storageType;
    private final File storageLocation;
    private final int autosaveInterval;

    public ServerConfiguration(StorageType storageType, File storageLocation, int maxRetries, int retryTimeout, int backlog, int autosaveInterval, boolean passwordEnabled, String password) {
        super(maxRetries, retryTimeout, passwordEnabled, password);
        this.storageType = storageType;
        this.storageLocation = storageLocation;
        this.backlog = backlog;
        this.autosaveInterval = autosaveInterval;
    }

    public int getBacklog() {
        return backlog;
    }
    
    public StorageType getStorageType() {
        return storageType;
    }
    
    public File getStorageLocation() {
        return storageLocation;
    }
    
    public int getAutosaveInterval() {
        return autosaveInterval;
    }
}
