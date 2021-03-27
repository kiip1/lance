package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class GetCommand extends Command {
    public GetCommand() {
        super("get", "Gets a value.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length == 0) {
            builder.setStatusCode(StatusCode.ERROR);
            builder.setMessage("Usage: get <key>");
        } else {
            try {
                Object value = handler.server.storage.get(args[0]);
                
                if (value instanceof JsonElement) {
                    builder.setJson((JsonElement) value);
                } else {
                    builder.setMessage(value == null ? null : new String((byte[]) value));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return getInternalErrorMessage(id);
            }
        }

        return builder.build();
    }
}
