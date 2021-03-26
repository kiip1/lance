package nl.kiipdevelopment.lance.configuration;

public class Configuration {
    private final int maxRetries;
    private final int retryTimeout;
    private final boolean passwordEnabled;
    private final String password;

    public Configuration(int maxRetries, int retryTimeout, boolean passwordEnabled, String password) {
        this.maxRetries = maxRetries;
        this.retryTimeout = retryTimeout;
        this.passwordEnabled = passwordEnabled;
        this.password = password;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getRetryTimeout() {
        return retryTimeout;
    }

    public boolean isPasswordEnabled() {
        return passwordEnabled;
    }

    public String getPassword() {
        return password;
    }
}
