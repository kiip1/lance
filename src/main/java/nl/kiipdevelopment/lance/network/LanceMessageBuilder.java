package nl.kiipdevelopment.lance.network;

import nl.kiipdevelopment.lance.server.command.Command;

public class LanceMessageBuilder {
    private int id;
    private StatusCode code;
    private String message;
    private Command command;

    public LanceMessageBuilder setId(int id) {
        this.id = id;

        return this;
    }

    public LanceMessageBuilder setStatusCode(StatusCode code) {
        this.code = code;

        return this;
    }

    public LanceMessageBuilder setObject(String message) {
        this.message = message;

        return this;
    }

    public LanceMessage build() {
        return new LanceMessage(id, code, message);
    }
}
