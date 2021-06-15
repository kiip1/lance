package nl.kiipdevelopment.lance;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new LanceServer();

        LanceClient client = new LanceClient();

        test2(client);
    }

    private static void test(LanceClient client) {
        Scanner scanner = new Scanner(System.in);
        String line;
        while ((line = scanner.nextLine()) != null) {
            String[] parts = line.split(" ");

            if (parts.length > 1) {
                String command = parts[0];
                String arguments = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

                if (command.equals("get")) {
                    System.out.println(new String(client.get(arguments)));
                } else if (command.equals("set")) {
                    String[] setParts = arguments.split(" ");
                    String key = setParts[0];
                    String value = String.join(" ", Arrays.copyOfRange(setParts, 1, setParts.length));

                    System.out.println(client.set(key, value.getBytes()));
                }
            }
        }
    }

    private static void test2(LanceClient client) {
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            byte[] next = new byte[4];
            random.nextBytes(next);

            boolean success = client.set(String.valueOf(i), next);
            Validate.ensure(success, "Fail (success) at " + i + ".");
            Validate.ensure(client.get(String.valueOf(i)) == next, "Fail (get) at " + i + ".");
        }
    }
}
