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
import java.util.concurrent.atomic.AtomicBoolean;

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
            .setMessage("Usage: batch <commands>");
        else {
            String[] commands = String.join(" ", args).split("/");

            AtomicBoolean success = new AtomicBoolean(true);

            try (Storage<?> storage = handler.server.storage) {
                for (String command : commands) {
                    LanceMessage lanceMessage = LanceMessage.getFromString(command);

                    if (lanceMessage != null) {
                        String[] parts = lanceMessage.getMessage().split(" ");

                        JsonElement json1 = lanceMessage.getJson();
                        String[] args1 = Arrays.copyOfRange(parts, 1, parts.length);

                        String key = args1[0];

                        if (storage.isJson() && (json1 != null || (args1.length == 2 && args1[1].equalsIgnoreCase("null"))))
                            ((JsonStorage) storage).set(key, json1);
                        else {
                            String value;

                            if (json1 == null)
                                value = String.join(" ", Arrays.copyOfRange(args1, 1, args1.length));
                            else
                                value = new Gson().toJson(json1);

                            if (storage instanceof FileStorage) {
                                try {
                                    ((FileStorage) storage).set(key, value.getBytes());
                                } catch (Exception e) {
                                    success.set(false);

                                    e.printStackTrace();
                                }
                            } else if (!value.equals("null")) {
                                ((JsonStorage) storage).set(key, new JsonPrimitive(value));
                            }
                        }
                    }
                }
            }

            builder.setMessage(success.get() ? "true" : "false");
        }

        return builder.build();
    }
}
