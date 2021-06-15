package nl.kiipdevelopment.lance.configuration;

import nl.kiipdevelopment.lance.storage.StorageType;

import java.io.File;

public class ServerConfiguration {
	public final int maxRetries;
	public final int retryTimeout;
	public final String password;
	public final boolean passwordEnabled;
	public final StorageType storageType;
	public final File storageLocation;
	public final int backlog;
	public final int autoSaveInterval;

	public ServerConfiguration(int maxRetries, int retryTimeout, String password, boolean passwordEnabled, StorageType storageType, File storageLocation, int backlog, int autoSaveInterval) {
		this.maxRetries = maxRetries;
		this.retryTimeout = retryTimeout;
		this.password = password;
		this.passwordEnabled = passwordEnabled;
		this.storageType = storageType;
		this.storageLocation = storageLocation;
		this.backlog = backlog;
		this.autoSaveInterval = autoSaveInterval;
	}
}
