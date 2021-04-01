package nl.kiipdevelopment.lance;

import com.google.gson.JsonPrimitive;
import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.server.LanceServer;

public class Main {
    public static void main(String[] args) {
        new LanceServer().start();

        LanceClient lanceClient = new LanceClient();

        lanceClient.start();

        int amount = 0;

        if (lanceClient.exists("amount"))
            amount = lanceClient.getJson("amount").getAsInt();

        lanceClient.setJson("amount", new JsonPrimitive(++amount));

        System.out.println(amount);
    }
}
