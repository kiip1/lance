package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.StatusCode;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

public class ResolvableListener<T> implements Listener {
    volatile LanceMessage value = null;

    private final Predicate<LanceMessage> filter;
    private final Function<LanceMessage, T> resolver;

    public ResolvableListener(int id, Function<LanceMessage, T> resolver) {
        this.filter = lanceMessage -> lanceMessage.getId() == id;
        this.resolver = resolver;
    }

    public ResolvableListener(Predicate<LanceMessage> filter, Function<LanceMessage, T> resolver) {
        this.filter = filter;
        this.resolver = resolver;
    }

    public T resolve(ListenerManager listenerManager, Runnable runnable) throws ErrorStatusException {
        try {
            listenerManager.executor.submit(runnable).get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }

        while (value == null)
            Thread.onSpinWait();

        listenerManager.client.setLastStatus(value.getCode());

        if (value.getCode() != StatusCode.OK)
            throw new ErrorStatusException(value);

        return resolver.apply(value);
    }

    @Override
    public boolean run(LanceMessage lanceMessage) {
        if (lanceMessage == null)
            return false;

        if (filter.test(lanceMessage)) {
            value = lanceMessage;

            return true;
        }

        return false;
    }
}