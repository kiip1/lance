package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ListenerManager extends Thread {
    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final LanceClient client;
    private final BufferedReader in;

    public ListenerManager(LanceClient client, BufferedReader in) {
        super("Lance-Listener-Manager");

        this.client = client;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = in.readLine()) != null) {
                @SuppressWarnings("unchecked")
                final ArrayList<Listener> tempListeners = (ArrayList<Listener>) listeners.clone();

                for (Listener listener : tempListeners) {
                    LanceMessage lanceMessage = LanceMessage.getFromString(line);

                    boolean success = listener.run(lanceMessage);

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

    public void close() {
        listeners.clear();
    }
}
