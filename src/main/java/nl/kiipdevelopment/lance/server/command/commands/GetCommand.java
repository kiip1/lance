package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;

public class GetCommand extends Command {
    public GetCommand() {
        super("get", "Gets a value.");
    }

    @Override
    public String execute(ServerConnectionHandler handler, String trigger, String[] args) {
        if (args.length == 0) {
            return "Usage: get <key>";
        }

        return "test";
    }
}
