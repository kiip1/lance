package nl.kiipdevelopment.lance.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListenerManager extends Thread {
    private final List<Listener> listenerList = new ArrayList<>();
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
                Iterator<Listener> listenerIterator = listenerList.iterator();

                while (listenerIterator.hasNext()) {
                    Listener listener = listenerIterator.next();

                    boolean success = listener.run(line);

                    if (success)
                        listenerIterator.remove();
                }
            }
        } catch (IOException e) {
            if (!e.getMessage().equals("Socket closed"))
                e.printStackTrace();
        }
    }

    public void listen(Listener listener) {
        listenerList.add(listener);
    }

    public <T> T resolve(ResolvableListener<T> listener) {
        listenerList.add(listener);

        return listener.resolve();
    }

    public void close() {
        listenerList.clear();
    }
}
