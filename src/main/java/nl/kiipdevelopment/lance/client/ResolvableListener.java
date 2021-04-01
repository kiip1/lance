package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.StatusCode;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ResolvableListener<T> implements Listener {
    volatile LanceMessage value = null;

    private final LanceClient client;
    private final Function<LanceMessage, Boolean> filter;
    private final Function<LanceMessage, T> resolver;

    public ResolvableListener(LanceClient client, int id, Function<LanceMessage, T> resolver) {
        this.client = client;
        this.filter = lanceMessage -> lanceMessage.getId() == id;
        this.resolver = resolver;
    }

    public ResolvableListener(LanceClient client, Function<LanceMessage, Boolean> filter, Function<LanceMessage, T> resolver) {
        this.client = client;
        this.filter = filter;
        this.resolver = resolver;
    }

    public T resolve(Runnable runnable) throws ErrorStatusException {
        try {
            Executors.newSingleThreadExecutor().submit(runnable).get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }

        while (value == null)
            Thread.onSpinWait();

        client.setLastStatus(value.getCode());

        if (value.getCode() != StatusCode.OK)
            throw new ErrorStatusException(value);

        return resolver.apply(value);
    }

    @Override
    public boolean run(LanceMessage lanceMessage) {
        if (lanceMessage == null)
            return false;

        if (lanceMessage.getMessage() == null)
            return false;

        if (filter.apply(lanceMessage)) {
            value = lanceMessage;

            return true;
        }

        return false;
    }
}