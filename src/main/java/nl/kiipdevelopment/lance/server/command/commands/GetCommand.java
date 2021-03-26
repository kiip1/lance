package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.LanceString;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class GetCommand extends Command {
    public GetCommand() {
        super("get", "Gets a value.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder
            .setId(id)
            .setStatusCode(StatusCode.OK);

        if (args.length == 0) {
            builder.setStatusCode(StatusCode.ERROR);

            builder.setObject(new LanceString("Usage: get <key>"));
        } else builder.setObject(new LanceString("test"));

        return builder.build();
    }
}
