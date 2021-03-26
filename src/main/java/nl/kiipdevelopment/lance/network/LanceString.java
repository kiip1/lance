package nl.kiipdevelopment.lance.network;

import org.jetbrains.annotations.NotNull;

public class LanceString implements LanceObject<String> {
    private final String string;

    public LanceString() {
        this("null");
    }

    public LanceString(@NotNull String string) {
        if (string.isEmpty()) this.string = "null";
        else this.string = string;
    }

    @Override
    public String get() {
        return string;
    }

    @Override
    public String getAsString() {
        return string;
    }

    @Override
    public LanceObject<String> getFromString(String string) {
        return new LanceString(string);
    }
}
