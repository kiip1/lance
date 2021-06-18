package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.connection.ConnectionHandler;
import nl.kiipdevelopment.lance.network.connection.ServerConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ClientPacket;

public abstract class ServerListener<T extends ClientPacket> extends Listener<T> {
    public ServerListener(T packet) {
        super(packet.id);
    }

    public final void execute(ConnectionHandler handler, T packet) {
        if (handler instanceof ServerConnectionHandler) {
            ServerConnectionHandler serverConnectionHandler = (ServerConnectionHandler) handler;

            execute(serverConnectionHandler, packet);
        }
    }

    public abstract void execute(ServerConnectionHandler handler, T packet);
}
