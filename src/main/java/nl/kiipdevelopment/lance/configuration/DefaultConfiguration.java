package nl.kiipdevelopment.lance.configuration;

public class DefaultConfiguration {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 6639;
    public static final int maxRetries = 10;
    public static final int retryTimeout = 1000;
    public static final int backlog = 50;
    public static final boolean passwordEnabled = false;
    public static final String password = null;

    public static Configuration getDefaultConfiguration() {
        return new Configuration(maxRetries, retryTimeout, passwordEnabled, password);
    }

    public static ServerConfiguration getDefaultServerConfiguration() {
        return new ServerConfiguration(maxRetries, retryTimeout, backlog, passwordEnabled, password);
    }
}
