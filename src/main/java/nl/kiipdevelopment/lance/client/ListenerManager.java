package nl.kiipdevelopment.lance.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ListenerManager extends Thread {
    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final BufferedReader in;

    public ListenerManager(BufferedReader in) {
        super("Lance-Listener-Manager");

        this.in = in;
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = in.readLine()) != null) {
                final ArrayList<Listener> tempListeners = (ArrayList<Listener>) listeners.clone();

                for (Listener listener : tempListeners) {
                    boolean success = listener.run(line);

                    if (success)
                        listeners.remove(listener);
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

    public <T> T resolve(ResolvableListener<T> listener) {
        listen(listener);

        return listener.resolve();
    }

    public void close() {
        listeners.clear();
    }
}
