package nl.kiipdevelopment.lance.server.storage;

public class StorageException extends Exception {
	public StorageException(String message) {
		super(message);
	}
	
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public StorageException(Throwable cause) {
		super(cause);
	}
}
