package nl.kiipdevelopment.lance.server.storage;

public interface Storage<V> extends AutoCloseable {
	V get(String key);
	
	boolean exists(String key);
	
	void set(String key, V value);
}
