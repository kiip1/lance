package nl.kiipdevelopment.lance.network;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LanceMessage {
    private final int id;
    private final StatusCode code;
    private final String message;

    public LanceMessage(int id, StatusCode code, String message) {
        this.id = id;
        this.code = code;
        this.message = message;
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

    @Override
    public String toString() {
        return id + " " + code.getId() + " " + encode(message);
    }

    public static LanceMessage getFromString(String string) {
        try {
            String[] parts = string.split(" ");

            int id = Integer.parseInt(parts[0]);
            StatusCode code = StatusCode.fromId(Integer.parseInt(parts[1]));
            String message = decode(parts[2]);

            return new LanceMessage(id, code, message);
        } catch (Exception e) {
            System.out.println("Invalid message: " + e.getMessage());
        }

        return null;
    }

    private static String encode(String decoded) {
        return Base64.getEncoder().encodeToString(decoded.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}
