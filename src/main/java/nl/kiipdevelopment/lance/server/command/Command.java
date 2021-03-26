package nl.kiipdevelopment.lance.server.command;

import nl.kiipdevelopment.lance.server.ServerConnectionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    public final List<String> triggers = new ArrayList<>();
    public final String description;

    public Command(String trigger, String description, String... aliases) {
        triggers.add(trigger);
        triggers.addAll(Arrays.asList(aliases));

        this.description = description;
    }

    public abstract String execute(ServerConnectionHandler handler, String trigger, String[] args);
}
