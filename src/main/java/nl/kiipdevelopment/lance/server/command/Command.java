package nl.kiipdevelopment.lance.server.command;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.StatusCode;
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

    public abstract LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args);
    
    public LanceMessage getInternalErrorMessage(int id) {
        return new LanceMessage(id, StatusCode.INTERNAL_ERROR);
    }
}
