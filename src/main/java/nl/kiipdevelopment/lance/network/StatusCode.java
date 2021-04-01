package nl.kiipdevelopment.lance.network;

public enum StatusCode {
    OK (0),
    ERROR (1),
    AUTH_REQUIRED (2),
    ACCESS_GRANTED (3),
    WRONG_PASSWORD (4),
    INTERNAL_ERROR (5),
    INVALID_MESSAGE (6),
    CLOSING (7);

    private final int id;

    StatusCode(int id) {
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
