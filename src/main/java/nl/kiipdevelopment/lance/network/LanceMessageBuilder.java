package nl.kiipdevelopment.lance.network;

import nl.kiipdevelopment.lance.server.command.Command;

import java.util.ArrayList;
import java.util.List;

public class LanceMessageBuilder {
    private int id;
    private StatusCode code;
    private Command command;
    private final List<String> arguments = new ArrayList<>();

    public LanceMessageBuilder setId(int id) {
        this.id = id;

        return this;
    }

    public LanceMessageBuilder setStatusCode(StatusCode code) {
        this.code = code;

        return this;
    }

    public LanceMessageBuilder setCommand(Command command) {
        this.command = command;

        return this;
    }

    public LanceMessageBuilder addArgument(String argument) {
        arguments.add(argument);

        return this;
    }

    public LanceMessage build() {
        return new LanceMessage(
            id,
            code,
            new LanceString(command.triggers.get(0) + " " + String.join(" ", arguments))
        );
    }
}
