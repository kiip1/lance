package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.storage.StorageType;

import java.io.File;

public class DefaultConfiguration {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 6639;
    
    public static final String name = "Lance-Client";
    public static final int maxRetries = 10;
    public static final int retryTimeout = 1000;
    public static final int backlog = 50;
    public static final int autoSaveInterval = 60;
    public static final boolean passwordEnabled = false;
    public static final String password = null;
    public static final StorageType storageType = StorageType.getDefault();
    public static final File storageLocation = storageType.getDefaultLocation();

    public static ClientConfiguration getClientConfiguration() {
        return new ClientConfiguration(
            name,
            maxRetries,
            retryTimeout,
            password
        );
    }

    public static ServerConfiguration getServerConfiguration() {
        return new ServerConfiguration(
            maxRetries,
            retryTimeout,
            password,
            passwordEnabled,
            storageType,
            storageLocation,
            backlog,
            autoSaveInterval
        );
    }
}
