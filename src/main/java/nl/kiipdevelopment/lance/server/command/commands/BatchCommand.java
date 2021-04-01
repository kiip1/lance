package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.command.CommandManager;

public class BatchCommand extends Command {
    public BatchCommand() {
        super("batch", "Sets multiple keys to multiple values.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length < 1) builder
            .setStatusCode(StatusCode.ERROR)
            .setMessage("Usage: batch <messages>");
        else {
            String[] messages = String.join(" ", args).split("/");

            boolean success = true;

            for (String message : messages) {
                LanceMessage lanceMessage = LanceMessage.getFromString(message);

                if (lanceMessage != null) {
                    boolean thisSuccess = CommandManager.handle(handler, id, lanceMessage).getCode() == StatusCode.OK;

                    if (!thisSuccess)
                        success = false;
                }
            }

            builder.setMessage(success ? "true" : "false");
        }

        return builder.build();
    }
}
