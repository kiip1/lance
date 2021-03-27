package nl.kiipdevelopment.lance.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.kiipdevelopment.lance.client.DataValue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class LanceMessage {
    private static final Gson GSON = new Gson();
    
    private final int id;
    private final StatusCode code;
    private String message;
    private JsonElement json;
    
    public LanceMessage(int id, StatusCode code, String message, JsonElement json) {
        this.id = id;
        this.code = code;
        this.message = message;
        this.json = json;
    }
    
    public LanceMessage(int id, StatusCode code, String message) {
        this.id = id;
        this.code = code;
        this.message = message;
    }
    
    public LanceMessage(int id, StatusCode code, JsonElement json) {
        this.id = id;
        this.code = code;
        this.json = json;
    }
    
    public LanceMessage(int id, StatusCode code) {
        this.id = id;
        this.code = code;
    }
    
    public int getId() {
        return id;
    }

    public StatusCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean hasMessage() {
        return message != null;
    }
    
    public JsonElement getJson() {
        return json;
    }
    
    public boolean hasJson() {
        return json != null;
    }
    
    public DataValue asDataValue() {
        if (hasJson()) {
            return new DataValue(json);
        } else {
            return new DataValue(message);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(id).append(" ").append(code.getId()).append(" ").append(hasJson() ? 1 : 0).append(hasMessage() ? 1 : 0);
        
        if (hasJson()) {
            result.append(" ").append(encode(GSON.toJson(json)));
        }
        if (hasMessage()) {
            result.append(" ").append(encode(message));
        }
        
        return result.toString();
    }

    public static LanceMessage getFromString(String string) {
        try {
            String[] parts = string.split(" ");

            int id = Integer.parseInt(parts[0]);
            StatusCode code = StatusCode.fromId(Integer.parseInt(parts[1]));
            
            String flags = parts[2];
            int hasJson = Integer.parseInt(flags.substring(0, 1));
            int hasMessage = Integer.parseInt(flags.substring(1, 2));
            
            JsonElement json = null;
            String message = null;
            
            if (hasJson == 1) {
                String jsonPart = parts[3];
                json = JsonParser.parseString(decode(jsonPart));
            }
            
            if (hasMessage == 1) {
                String messagePart = hasJson == 1 ? parts[4] : parts[3];
                message = decode(messagePart);
            }

            return new LanceMessage(id, code, message, json);
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "Invalid message: " + e.getMessage());
        }

        return null;
    }

    private static String encode(String decoded) {
        return Base64
            .getEncoder()
            .encodeToString(Objects.requireNonNullElse(
                decoded,
                "null"
            ).getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String encoded) {
        if (encoded == null)
            return "null";

        return new String(Base64.getDecoder().decode(encoded));
    }
}
