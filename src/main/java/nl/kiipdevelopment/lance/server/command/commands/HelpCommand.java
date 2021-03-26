package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.command.CommandManager;

import java.util.stream.Collectors;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Lists all commands and their descriptions.", "?");
    }

    @Override
    public String execute(ServerConnectionHandler handler, String trigger, String[] args) {
        return CommandManager
            .getCommandMap()
            .keySet()
            .stream()
            .map(key -> key + " - " + CommandManager.get(key).description)
            .collect(Collectors.joining("\n"));
    }
}
