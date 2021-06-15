package nl.kiipdevelopment.lance.configuration;

public class ClientConfiguration {
	public final String name;
	public final int maxRetries;
	public final int retryTimeout;
	public final String password;

	ClientConfiguration(String name, int maxRetries, int retryTimeout, String password) {
		this.name = name;
		this.maxRetries = maxRetries;
		this.retryTimeout = retryTimeout;
		this.password = password;
	}
}