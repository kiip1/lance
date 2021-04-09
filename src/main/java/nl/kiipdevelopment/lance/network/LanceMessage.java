package nl.kiipdevelopment.lance.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.kiipdevelopment.lance.client.DataValue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        StringBuilder result = new StringBuilder();

        result
            .append(id)
            .append(" ")
            .append(code.getId())
            .append(" ");

        result.append(
            (hasJson() ? 2 : 0) +
            (hasMessage() ? 1 : 0)
        );
        
        if (hasJson())
            result.append(" ").append(encode(GSON.toJson(json)));

        if (hasMessage())
            result.append(" ").append(encode(message));

        return result.toString();
    }

    public static LanceMessage getFromString(String string) {
        try {
            String[] parts = string.split(" ");

            int id = Integer.parseInt(parts[0]);
            StatusCode code = StatusCode.fromId(Integer.parseInt(parts[1]));
            String flags = String.format("%2s", Integer.toBinaryString(
                Integer.parseInt(parts[2]))).replace(' ', '0');

            boolean hasJson = flags.charAt(0) == '1'; // 10
            boolean hasMessage = flags.charAt(1) == '1'; // 01
            
            JsonElement json = null;
            String message = null;
            
            if (hasJson) {
                String jsonPart = parts[3];

                json = new JsonParser().parse(decode(jsonPart));
            }

            if (hasMessage) {
                String messagePart = hasJson ? parts[4] : parts[3];

                message = decode(messagePart);
            }

            return new LanceMessage(
                id,
                code,
                message,
                json
            );
        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "Invalid message: " + e);
        }

        return null;
    }

    private static String encode(String decoded) {
        if (decoded == null)
            decoded = "null";
        
        return Base64
            .getEncoder()
            .encodeToString(decoded
            .getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String encoded) {
        if (encoded == null)
            return "null";

        return new String(Base64.getDecoder().decode(encoded));
    }
}
