package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.storage.FileStorage;
import nl.kiipdevelopment.lance.server.storage.JsonStorage;
import nl.kiipdevelopment.lance.server.storage.Storage;

import java.util.Arrays;

public class SetCommand extends Command {
    public SetCommand() {
        super("set", "Sets a key to a value.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length < 2) {
            builder.setStatusCode(StatusCode.ERROR);
            builder.setMessage("Usage: set <key> <value>");
        } else {
            String key = args[0];

            try {
                Storage<?> storage = handler.server.storage;
                if (storage.isJson() && json != null) {
                    ((JsonStorage) storage).set(key, json);
                } else {
                    String value;
                    if (json == null) {
                        value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    } else {
                        //When we have file based storage, but the command is for json, convert it to a string
                        value = new Gson().toJson(json);
                    }

                    if (storage instanceof FileStorage) {
                        ((FileStorage) storage).set(key, value.getBytes());
                    } else if (!value.equals("null")) {
                        ((JsonStorage) storage).set(key, new JsonPrimitive(value));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return getInternalErrorMessage(id);
            }
        }

        return builder.build();
    }
}
