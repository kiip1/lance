package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.listener.listeners.server.*;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;

import java.util.*;

public class ServerListenerManager {
    public static final Map<Byte, ServerListener> listeners = new HashMap<>();
    private static boolean initialised = false;

    public static void init() {
        if (!initialised) {
            initialised = true;

            ServerListenerManager.register(
                new CloseConnectionServerListener(),
                new ExistsServerListener(),
                new GetServerListener(),
                new ListServerListener(),
                new SetServerListener(),
                new StopServerListener(),
                new SwitchingStorageServerListener()
            );
        }
    }

    public static void register(ServerListener serverListener) {
        listeners.put(serverListener.id, serverListener);
    }

    public static void register(ServerListener serverListener, ServerListener... serverListeners) {
        List<ServerListener> serverListenerList = new ArrayList<>(Arrays.asList(serverListeners));

        serverListenerList.add(serverListener);

        serverListenerList.forEach(ServerListenerManager::register);
    }

    public static void handle(ServerConnectionHandler handler, ClientPacket packet) {
        ServerListener serverListener = listeners.get(packet.id);

        if (serverListener == null) {
            handler.close("There is no listener for packet with id " + packet.id + ".");
        } else {
            serverListener.execute(handler, packet);
        }
    }
}
