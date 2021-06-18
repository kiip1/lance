package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.connection.ConnectionHandler;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.listener.listeners.client.ClientCloseConnectionListener;
import nl.kiipdevelopment.lance.network.listener.listeners.client.ClientHeartbeatListener;
import nl.kiipdevelopment.lance.network.listener.listeners.server.*;
import nl.kiipdevelopment.lance.network.packet.Packet;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;

import java.util.*;

public class ListenerManager {
    public static final Map<Byte, Listener<?>> listeners = new HashMap<>();
    private static boolean initialisedClient = false;
    private static boolean initialisedServer = false;

    public static void initClient() {
        if (!initialisedClient) {
            initialisedClient = true;

            ListenerManager.register(
                new ClientCloseConnectionListener(),
                new ClientHeartbeatListener()
            );
        }
    }

    public static void initServer() {
        if (!initialisedServer) {
            initialisedServer = true;

            ListenerManager.register(
                new ServerCloseConnectionListener(),
                new ServerExistsListener(),
                new ServerGetListener(),
                new ServerHeartbeatListener(),
                new ServerListListener(),
                new ServerSetListener(),
                new ServerSwitchingStorageListener()
            );
        }
    }

    public static void register(Listener<?> listener) {
        listeners.put(listener.id, listener);
    }

    public static void register(Listener<?> listener, Listener<?>... listeners) {
        List<Listener<?>> listenerList = new ArrayList<>(Arrays.asList(listeners));

        listenerList.add(listener);

        listenerList.forEach(ListenerManager::register);
    }

    public static void handle(ConnectionHandler handler, Packet packet) {
        //noinspection rawtypes
        Listener listener = listeners.get(packet.id);

        if (listener == null) {
            if (handler instanceof ClientConnectionHandler) {
                ClientConnectionHandler clientConnectionHandler = ((ClientConnectionHandler) handler);

                clientConnectionHandler.satisfy((ServerPacket) packet);
            } else if (handler instanceof ServerConnectionHandler) {
                ServerConnectionHandler serverConnectionHandler = ((ServerConnectionHandler) handler);

                serverConnectionHandler.close("There is no listener for packet with id " + packet.id + ".");
            }
        } else {
            //noinspection unchecked
            listener.execute(handler, packet);
        }
    }
}
