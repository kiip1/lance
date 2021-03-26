package nl.kiipdevelopment.lance.server.command;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceString;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.commands.ConnectionsCommand;
import nl.kiipdevelopment.lance.server.command.commands.GetCommand;
import nl.kiipdevelopment.lance.server.command.commands.HelpCommand;
import nl.kiipdevelopment.lance.server.command.commands.StopCommand;

import java.util.*;

public class CommandManager {
    private static final Map<String, Command> commandMap = new HashMap<>();

    public static void init() {
        CommandManager.register(
            new ConnectionsCommand(),
            new GetCommand(),
            new HelpCommand(),
            new StopCommand()
        );
    }

    public static void register(Command command) {
        command.triggers.forEach(trigger -> commandMap.put(trigger, command));
    }

    public static void register(Command command, Command... commands) {
        List<Command> commandList = new ArrayList<>(Arrays.asList(commands));

        commandList.add(command);

        commandList.forEach(CommandManager::register);
    }

    public static Command get(String key) {
        return commandMap.get(key);
    }

    public static LanceMessage handle(ServerConnectionHandler handler, int id, LanceMessage lanceMessage) {
        String[] parts = lanceMessage.getObject().getAsString().split(" ");

        String trigger = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = commandMap.get(trigger);

        if (command == null) return unknownCommand(id);
        else return new LanceMessage(
            id,
            StatusCode.OK,
            new LanceString(command.execute(handler, trigger, args))
        );
    }

    public static Map<String, Command> getCommandMap() {
        return commandMap;
    }

    private static LanceMessage unknownCommand(int id) {
        return new LanceMessage(
            id,
            StatusCode.ERROR,
            new LanceString("That command doesn't exist. Type \"help\" for help.")
        );
    }
}
