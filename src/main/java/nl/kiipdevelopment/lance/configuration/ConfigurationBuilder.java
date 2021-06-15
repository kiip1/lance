package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.storage.StorageType;

import java.io.File;

public class ConfigurationBuilder {
    private String name = DefaultConfiguration.name;
    private int maxRetries = DefaultConfiguration.maxRetries;
    private int retryTimeout = DefaultConfiguration.retryTimeout;
    private int backlog = DefaultConfiguration.backlog;
    private int autoSaveInterval = DefaultConfiguration.autoSaveInterval;
    private boolean passwordEnabled = DefaultConfiguration.passwordEnabled;
    private String password = DefaultConfiguration.password;
    private StorageType storageType = DefaultConfiguration.storageType;
    private File storageLocation = DefaultConfiguration.storageLocation;

    public ConfigurationBuilder setName(String name) {
        this.name = name;

        return this;
    }

    public ConfigurationBuilder setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;

        return this;
    }

    public ConfigurationBuilder setBackLog(int backlog) {
        this.backlog = backlog;

        return this;
    }

    public ConfigurationBuilder setRetryTimeout(int retryTimeout) {
        this.retryTimeout = retryTimeout;

        return this;
    }

    public ConfigurationBuilder setPassword(String password) {
        this.passwordEnabled = true;
        this.password = password;

        return this;
    }

    public ConfigurationBuilder setAutoSaveInterval(int autoSaveInterval) {
        this.autoSaveInterval = autoSaveInterval;

        return this;
    }
    
    public ConfigurationBuilder setStorageType(StorageType storageType) {
        this.storageType = storageType;

        return this;
    }
    
    public ConfigurationBuilder setStorageLocation(File storageLocation) {
        this.storageLocation = storageLocation;

        return this;
    }

    public ClientConfiguration buildClientConfiguration() {
        return new ClientConfiguration(
            name,
            maxRetries,
            retryTimeout,
            password
        );
    }

    public ServerConfiguration buildServerConfiguration() {
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
