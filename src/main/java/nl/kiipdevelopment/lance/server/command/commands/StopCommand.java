package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop", "Stops the server.", "end", "shutdown");
    }

    @Override
    public String execute(ServerConnectionHandler handler, String trigger, String[] args) {
        handler.server.shutdown();

        return "Server is shutting down.";
    }
}
