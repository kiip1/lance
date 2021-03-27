package nl.kiipdevelopment.lance.network;

import com.google.gson.JsonElement;

public class LanceMessageBuilder {
    private int id;
    private StatusCode code;
    private String message;
    private JsonElement json;

    public LanceMessageBuilder setId(int id) {
        this.id = id;

        return this;
    }

    public LanceMessageBuilder setStatusCode(StatusCode code) {
        this.code = code;

        return this;
    }

    public LanceMessageBuilder setMessage(String message) {
        this.message = message;

        return this;
    }
    
    public LanceMessageBuilder setJson(JsonElement json) {
        this.json = json;
        
        return this;
    }

    public LanceMessage build() {
        return new LanceMessage(id, code, message, json);
    }
}
