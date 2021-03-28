package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.server.storage.StorageType;

import java.nio.file.Path;

public class ConfigurationBuilder {
    private int maxRetries = DefaultConfiguration.maxRetries;
    private int retryTimeout = DefaultConfiguration.retryTimeout;
    private int backlog = DefaultConfiguration.backlog;
    private int autosaveInterval = DefaultConfiguration.autosaveInterval;
    private boolean passwordEnabled = DefaultConfiguration.passwordEnabled;
    private String password = DefaultConfiguration.password;
    private StorageType storageType = DefaultConfiguration.storageType;
    private Path storageLocation = DefaultConfiguration.storageLocation;

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
    
    /**
     * In seconds
     */
    public void setAutosaveInterval(int autosaveInterval) {
        this.autosaveInterval = autosaveInterval;
    }
    
    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }
    
    public void setStorageLocation(Path storageLocation) {
        this.storageLocation = storageLocation;
    }
    
    public Configuration build() {
        return new Configuration(maxRetries, retryTimeout, passwordEnabled, password);
    }

    public ServerConfiguration buildServerConfiguration() {
        return new ServerConfiguration(storageType, storageLocation, maxRetries, retryTimeout, autosaveInterval, backlog, passwordEnabled, password);
    }
}
