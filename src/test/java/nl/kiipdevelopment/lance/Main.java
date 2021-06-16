package nl.kiipdevelopment.lance;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        new LanceServer();

        LanceClient client = new LanceClient();

        test(client);
    }

    private static void test(LanceClient client) {
        Scanner scanner = new Scanner(System.in);
        String line;
        while ((line = scanner.nextLine()) != null) {
            String[] parts = line.split(" ");

            if (parts.length > 1) {
                String command = parts[0];
                String arguments = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

                switch (command) {
                    case "get" -> System.out.println(client.getJson(arguments));
                    case "set" -> {
                        String[] setParts = arguments.split(" ");
                        String key = setParts[0];
                        String value = String.join(" ", Arrays.copyOfRange(setParts, 1, setParts.length));
                        System.out.println(client.setJson(key, gson.fromJson(value, JsonElement.class)));
                    }
                    case "exists" -> System.out.println(client.existsJson(arguments));
                    case "list" -> System.out.println(String.join("\n", client.listJson(arguments)));
                }
            }
        }
    }

    private static void test2(LanceClient client) {
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            byte[] next = new byte[4];
            random.nextBytes(next);

            JsonArray jsonArray = new JsonArray();

            for (byte b : next) {
                jsonArray.add(b);
            }

            // Equality Check
            boolean success = client.setJson(String.valueOf(i), jsonArray);
            JsonArray jsonArray1 = client.getJson(String.valueOf(i)).getAsJsonArray();

            Validate.ensure(success, "Fail (success) at " + i + ".");
            Validate.ensure(jsonArray1.equals(jsonArray), "Fail (get) at " + i + ".");

            // Content check
            byte[] array = new byte[jsonArray1.size()];

            for (int j = 0; j < array.length; j++) {
                array[j] = jsonArray.get(j).getAsJsonPrimitive().getAsByte();
            }

            Validate.ensure(Arrays.equals(next, array), "Fail (content) at " + i + ".");
            System.out.println(new String(next));
            System.out.println(new String(array));
        }
    }
}
