package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.storage.Storage;
import nl.kiipdevelopment.lance.server.storage.StorageType;

import java.util.Arrays;

public class SetCommand extends Command {
    public SetCommand() {
        super("set", "Sets a key to a value.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length < 2) {
            builder
                .setStatusCode(StatusCode.ERROR)
                .setMessage("Usage: set <key> <value>");
        } else {
            String key = args[0];
            String value = String.join(" ", Arrays.copyOfRange(
                args,
                1,
                args.length
            ));

            try (Storage<JsonElement> storage = Storage.getStorage(StorageType.JSON)) {
                JsonElement jsonElement = JsonParser.parseString(value);

                storage.set(key, jsonElement);

                builder.setMessage(jsonElement.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }
}
