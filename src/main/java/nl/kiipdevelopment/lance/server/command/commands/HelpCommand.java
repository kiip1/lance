package nl.kiipdevelopment.lance.server.command.commands;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.LanceString;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.command.CommandManager;

import java.util.stream.Collectors;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Lists all commands and their descriptions.", "?");
    }

    @Override
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, String[] args) {
        return new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setObject(new LanceString(CommandManager
                .getCommandMap()
                .keySet()
                .stream()
                .map(key -> key + " - " + CommandManager.get(key).description)
                .collect(Collectors.joining("\n"))))
            .build();
    }
}
