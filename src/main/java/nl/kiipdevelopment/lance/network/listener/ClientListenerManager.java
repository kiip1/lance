package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.listener.listeners.client.ClientCloseConnectionListener;
import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.listeners.client.ClientHeartbeatListener;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.util.*;

public class ClientListenerManager {
    public static final Map<Byte, ClientListener<?>> listeners = new HashMap<>();
    private static boolean initialised = false;

    public static void init() {
        if (!initialised) {
            initialised = true;

            ClientListenerManager.register(
                new ClientCloseConnectionListener(),
                new ClientHeartbeatListener()
            );
        }
    }

    public static void register(ClientListener<?> clientListener) {
        listeners.put(clientListener.id, clientListener);
    }

    public static void register(ClientListener<?> clientListener, ClientListener<?>... clientListeners) {
        List<ClientListener<?>> clientListenerList = new ArrayList<>(Arrays.asList(clientListeners));

        clientListenerList.add(clientListener);

        clientListenerList.forEach(ClientListenerManager::register);
    }

    public static void handle(ClientConnectionHandler handler, ServerPacket packet) {
        //noinspection rawtypes
        ClientListener clientListener = listeners.get(packet.id);

        if (clientListener == null) {
            handler.satisfy(packet);
        } else {
            //noinspection unchecked
            clientListener.execute(handler, packet);
        }
    }
}
