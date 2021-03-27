package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class ExistsCommand extends Command {
    public ExistsCommand() {
        super("exists", "Checks if a key exists.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length == 0) {
            builder.setStatusCode(StatusCode.ERROR);
            builder.setMessage("Usage: exists <key>");
        } else {
            try {
                builder.setMessage(handler.server.storage.exists(args[0]) ? "true" : "false");
            } catch (Exception e) {
                e.printStackTrace();
                return getInternalErrorMessage(id);
            }
        }

        return builder.build();
    }
}
