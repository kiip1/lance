package nl.kiipdevelopment.lance.network;

public enum StatusCode {
    OK ((byte) 0),
    ERROR ((byte) 1),
    AUTH_REQUIRED ((byte) 2),
    ACCESS_GRANTED ((byte) 3),
    WRONG_PASSWORD ((byte) 4),
    INTERNAL_ERROR ((byte) 5),
    INVALID_MESSAGE ((byte) 6),
    CLOSING ((byte) 7);

    private final byte id;

    StatusCode(byte id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static StatusCode fromId(int id) {
        for (StatusCode code : StatusCode.values())
            if (code.id == id)
                return code;

        return null;
    }
}
