package nl.kiipdevelopment.lance.network.listener;

import nl.kiipdevelopment.lance.network.connection.ClientConnectionHandler;
import nl.kiipdevelopment.lance.network.connection.ConnectionHandler;
import nl.kiipdevelopment.lance.network.packet.ServerPacket;

public abstract class ClientListener<T extends ServerPacket> extends Listener<T> {
    public ClientListener(T packet) {
        super(packet.id);
    }

    public final void execute(ConnectionHandler handler, T packet) {
        if (handler instanceof ClientConnectionHandler) {
            ClientConnectionHandler clientConnectionHandler = (ClientConnectionHandler) handler;

            execute(clientConnectionHandler, packet);
        }
    }

    public abstract void execute(ClientConnectionHandler handler, T packet);
}
