package nl.kiipdevelopment.lance;

import com.google.gson.JsonPrimitive;
import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.server.LanceServer;

public class Main {
    public static void main(String[] args) {
        new LanceServer().start();

        LanceClient client = new LanceClient();

        client.start();

        int amount = 10000;

        // Only run 1 at a time, comment the other.
        // Do this to make sure it's fair.
        // If you don't do this, the last one you run will always be the fastest.

        with(client, amount);
        // without(client, amount);
    }

    private static void with(LanceClient client, int amount) {
        long start = System.currentTimeMillis();

        client.batch(() -> {
            for (int i = 0; i < amount; i++)
                client.setJson("key" + i, new JsonPrimitive("value" + i));
        });

        System.out.println(amount + " with batch: " + (System.currentTimeMillis() - start) + "ms.");
    }

    private static void without(LanceClient client, int amount) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < amount; i++)
            client.setJson("key" + i, new JsonPrimitive("value" + i));

        System.out.println(amount + " without batch: " + (System.currentTimeMillis() - start) + "ms.");
    }
}
