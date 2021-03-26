package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class ConnectionsCommand extends Command {
    public ConnectionsCommand() {
        super("connections", "Lists all connections.", "list", "listconnections");
    }

    @Override
    public String execute(ServerConnectionHandler handler, String trigger, String[] args) {
        return String.join(
            "\n",
            handler.server.connections
        );
    }
}
