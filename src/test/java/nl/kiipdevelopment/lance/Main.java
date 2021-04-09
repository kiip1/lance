package nl.kiipdevelopment.lance;

import com.google.gson.JsonPrimitive;
import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.client.LanceConsoleClient;
import nl.kiipdevelopment.lance.server.LanceServer;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        new LanceServer().start();

        LanceClient client = new LanceConsoleClient();

        client.start();

        int amount = 10000;

        // Only run 1 at a time, comment the other.
        // Do this to make sure it's fair.
        // If you don't do this, the last one you run will always be the fastest.

        // with(client, amount);
        // without(client, amount);
    }

    private static void with(LanceClient client, int amount) {
        long start = System.currentTimeMillis();

        client.batch(() -> {
            for (int i = 0; i < amount; i++)
                client.setJson("keyw" + i, new JsonPrimitive(UUID.randomUUID() + "/" + System.currentTimeMillis()));
        });

        System.out.println(amount + " with batch: " + (System.currentTimeMillis() - start) + "ms.");
    }

    private static void without(LanceClient client, int amount) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < amount; i++)
            client.setJson("keyo" + i, new JsonPrimitive(UUID.randomUUID() + "/" + System.currentTimeMillis()));

        System.out.println(amount + " without batch: " + (System.currentTimeMillis() - start) + "ms.");
    }
}
