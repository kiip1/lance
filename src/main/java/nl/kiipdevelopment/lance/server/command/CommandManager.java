package nl.kiipdevelopment.lance.server.command;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.commands.*;

import java.util.*;

public class CommandManager {
    private static final Map<String, Command> commandMap = new HashMap<>();

    public static void init() {
        CommandManager.register(
            new BatchCommand(),
            new ConnectionsCommand(),
            new ExistsCommand(),
            new GetCommand(),
            new HelpCommand(),
            new SetCommand(),
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
        if (!lanceMessage.hasMessage()) return new LanceMessage(id, StatusCode.ERROR, "Not a command");
        
        String[] parts = lanceMessage.getMessage().split(" ");

        String trigger = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = commandMap.get(trigger);

        if (command == null) return unknownCommand(id);
        else return command.execute(handler, id, trigger, lanceMessage.getJson(), args);
    }

    public static Map<String, Command> getCommandMap() {
        return commandMap;
    }

    private static LanceMessage unknownCommand(int id) {
        return new LanceMessage(
            id,
            StatusCode.ERROR,
            "That command doesn't exist. Type \"help\" for help."
        );
    }
}
