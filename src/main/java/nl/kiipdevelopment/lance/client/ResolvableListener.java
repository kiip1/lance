package nl.kiipdevelopment.lance.client;

public interface ResolvableListener<T> extends Listener {
    T resolve();
}
