package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
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
    public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args) {
        return new LanceMessageBuilder()
            .setId(id)
            .setStatusCode(StatusCode.OK)
            .setMessage(CommandManager
                .getCommandMap()
                .keySet()
                .stream()
                .map(key -> key + " - " + CommandManager.get(key).description)
                .collect(Collectors.joining("\n")))
            .build();
    }
}
