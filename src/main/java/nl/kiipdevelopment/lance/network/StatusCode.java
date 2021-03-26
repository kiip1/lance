package nl.kiipdevelopment.lance.network;

public enum StatusCode {
    OK (0),
    ERROR (1),
    CLOSING (2);

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
