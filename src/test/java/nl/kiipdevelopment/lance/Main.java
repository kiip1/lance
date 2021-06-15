package nl.kiipdevelopment.lance;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
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
}
