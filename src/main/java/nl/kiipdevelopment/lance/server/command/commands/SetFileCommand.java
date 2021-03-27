package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.storage.Storage;
import nl.kiipdevelopment.lance.server.storage.StorageType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SetFileCommand extends Command {
    public SetFileCommand() {
        super("setfile", "Sets a key to a file.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length < 2) {
            builder
                .setStatusCode(StatusCode.ERROR)
                .setMessage("Usage: setfile <key> <value>");
        } else {
            String key = args[0];
            byte[] value = String.join(" ", Arrays.copyOfRange(
                args,
                1,
                args.length
            )).getBytes(StandardCharsets.UTF_8);

            try (Storage<byte[]> storage = Storage.getStorage(StorageType.FILE)) {
                storage.set(key, value);

                builder.setMessage(Arrays.toString(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }
}
