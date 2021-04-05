package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

public class ListenerManager extends Thread {
    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final BufferedReader in;

    public final LanceClient client;
    public final ThreadPoolExecutor executor;

    public ListenerManager(LanceClient client, BufferedReader in) {
        super("Lance-Listener-Manager");

        this.client = client;
        this.in = in;
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = in.readLine()) != null) {
                @SuppressWarnings("unchecked")
                final ArrayList<Listener> tempListeners = (ArrayList<Listener>) listeners.clone();

                for (Listener listener : tempListeners) {
                    final String finalLine = line;

                    executor.submit(() -> {
                        LanceMessage lanceMessage = LanceMessage.getFromString(finalLine);

                        boolean success = listener.run(lanceMessage);

                        if (success)
                            listeners.remove(listener);
                    });
                }
            }

            close();
        } catch (IOException e) {
            if (!e.getMessage().equals("Socket closed"))
                e.printStackTrace();
        }
    }

    public void listen(Listener listener) {
        listeners.add(listener);
    }

    public <T> T resolve(ResolvableListener<T> listener, Runnable runnable, Consumer<LanceMessage> errorHandler) {
        listen(listener);

        try {
            return listener.resolve(this, runnable);
        } catch (ErrorStatusException e) {
            errorHandler.accept(e.getLanceMessage());
        }

        return null;
    }

    public void close() {
        listeners.clear();
    }
}
