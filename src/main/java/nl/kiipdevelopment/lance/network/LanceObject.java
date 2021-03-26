package nl.kiipdevelopment.lance.network;

public interface LanceObject<T> {
    T get();

    String getAsString();

    LanceObject<?> getFromString(String string);
}
