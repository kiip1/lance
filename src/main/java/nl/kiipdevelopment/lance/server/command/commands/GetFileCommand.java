package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.storage.Storage;
import nl.kiipdevelopment.lance.server.storage.StorageType;

public class GetFileCommand extends Command {
    public GetFileCommand() {
        super("getfile", "Gets a file.");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        LanceMessageBuilder builder = new LanceMessageBuilder();

        builder.setId(id).setStatusCode(StatusCode.OK);

        if (args.length == 0) {
            builder
                .setStatusCode(StatusCode.ERROR)
                .setMessage("Usage: getfile <key>");
        } else {
            try (Storage<byte[]> storage = Storage.getStorage(StorageType.FILE)) {
                byte[] value = storage.get(args[0]);

                builder.setMessage(new String(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }
}
