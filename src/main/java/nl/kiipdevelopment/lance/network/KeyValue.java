package nl.kiipdevelopment.lance.network;

public class KeyValue<A, B> {
    private A key;
    private B value;

    public KeyValue(A key, B value) {
        this.key = key;
        this.value = value;
    }

    public A getKey() {
        return key;
    }

    public B getValue() {
        return value;
    }

    public void setKey(A key) {
        this.key = key;
    }

    public void setValue(B value) {
        this.value = value;
    }
}
