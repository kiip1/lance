package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.LanceString;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop", "Stops the server.", "end", "shutdown");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        handler.server.shutdown();

        return new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setObject(new LanceString("Server is shutting down."))
            .build();
    }
}
