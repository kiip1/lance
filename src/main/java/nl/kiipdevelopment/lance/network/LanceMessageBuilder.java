package nl.kiipdevelopment.lance.network;

import nl.kiipdevelopment.lance.server.command.Command;

public class LanceMessageBuilder {
    private int id;
    private StatusCode code;
    private LanceObject<?> object;
    private Command command;

    public LanceMessageBuilder setId(int id) {
        this.id = id;

        return this;
    }

    public LanceMessageBuilder setStatusCode(StatusCode code) {
        this.code = code;

        return this;
    }

    public LanceMessageBuilder setObject(LanceObject<?> object) {
        this.object = object;

        return this;
    }

    public LanceMessage build() {
        return new LanceMessage(
            id,
            code,
            object
        );
    }
}
