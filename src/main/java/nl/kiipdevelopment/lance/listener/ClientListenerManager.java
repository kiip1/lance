package nl.kiipdevelopment.lance.listener;

import nl.kiipdevelopment.lance.listener.listeners.client.CloseConnectionClientListener;
import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.util.*;

public class ClientListenerManager {
    public static final Map<Byte, ClientListener> listeners = new HashMap<>();
    private static boolean initialised = false;

    public static void init() {
        if (!initialised) {
            initialised = true;

            ClientListenerManager.register(
                new CloseConnectionClientListener()
            );
        }
    }

    public static void register(ClientListener clientListener) {
        listeners.put(clientListener.id, clientListener);
    }

    public static void register(ClientListener clientListener, ClientListener... clientListeners) {
        List<ClientListener> clientListenerList = new ArrayList<>(Arrays.asList(clientListeners));

        clientListenerList.add(clientListener);

        clientListenerList.forEach(ClientListenerManager::register);
    }

    public static void handle(ClientConnectionHandler handler, ServerPacket packet) {
        ClientListener clientListener = listeners.get(packet.id);

        if (clientListener == null) {
            handler.satisfy(packet);
        } else {
            clientListener.execute(handler, packet);
        }
    }
}
