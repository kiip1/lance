package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.server.storage.StorageType;

public class ServerConfiguration extends Configuration {
    private final int backlog;
    private final StorageType storageType;
    private final String storageLocation;

    public ServerConfiguration(StorageType storageType, String storageLocation, int maxRetries, int retryTimeout, int backlog, boolean passwordEnabled, String password) {
        super(maxRetries, retryTimeout, passwordEnabled, password);
        this.storageType = storageType;
        this.storageLocation = storageLocation;
    
        this.backlog = backlog;
    }

    public int getBacklog() {
        return backlog;
    }
    
    public StorageType getStorageType() {
        return storageType;
    }
    
    public String getStorageLocation() {
        return storageLocation;
    }
}
