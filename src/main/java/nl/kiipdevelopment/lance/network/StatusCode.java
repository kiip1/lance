package nl.kiipdevelopment.lance.network;

public enum StatusCode {
    OK (0),
    ERROR (1),
    AUTH_REQUIRED (2),
    ACCESS_GRANTED (3),
    INTERNAL_ERROR (4),
    INVALID_MESSAGE (5),
    CLOSING (6);

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
