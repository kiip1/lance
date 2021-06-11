package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.server.storage.StorageType;

import java.io.File;

public class ServerConfiguration extends Configuration {
    private final int backlog;
    private final StorageType storageType;
    private final File storageLocation;
    private final int autoSaveInterval;

    public ServerConfiguration(StorageType storageType, File storageLocation, int maxRetries, int retryTimeout, int backlog, int autoSaveInterval, boolean passwordEnabled, String password) {
        super(maxRetries, retryTimeout, passwordEnabled, password);

        this.storageType = storageType;
        this.storageLocation = storageLocation;
        this.backlog = backlog;
        this.autoSaveInterval = autoSaveInterval;
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
    
    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }
}
