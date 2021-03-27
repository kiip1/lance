package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.storage.Storage;
import nl.kiipdevelopment.lance.server.storage.StorageType;

public class ExistsFileCommand extends Command {
    public ExistsFileCommand() {
        super("existsfile", "Checks if a key exists.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length == 0) {
            builder
                .setStatusCode(StatusCode.ERROR)
                .setMessage("Usage: existsfile <key>");
        } else {
            String key = args[0];

            try (Storage<byte[]> storage = Storage.getStorage(StorageType.FILE)) {
                builder.setMessage(storage.exists(key) ? "true" : "false");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }
}
