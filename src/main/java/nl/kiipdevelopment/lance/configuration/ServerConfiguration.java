package nl.kiipdevelopment.lance.configuration;

public class ServerConfiguration extends Configuration {
    private final int backlog;

    public ServerConfiguration(int maxRetries, int retryTimeout, int backlog, boolean passwordEnabled, String password) {
        super(maxRetries, retryTimeout, passwordEnabled, password);

        this.backlog = backlog;
    }

    public int getBacklog() {
        return backlog;
    }
}
