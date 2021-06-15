package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(
                "##\n" +
                "##\n" +
                "##           ########     ## ######      #######     #######\n" +
                "##          ##########    ##########    #########   #########\n" +
                "##          ##########    ##########   ##########  ###########\n" +
                "##          ###    ###    ###    ###   ###    ###  ###     ###\n" +
                "##                 ###    ###    ###   ###         ###     ###\n" +
                "##             #######    ###    ###   ###         ###########\n" +
                "##           #########    ##      ##   ###         ###########\n" +
                "##          ##########    ##      ##   ###         ###\n" +
                "##          ###    ####   ##      ##   ###         ###\n" +
                "##          ###    ####   ##      ##   ###    ###  ####    ###\n" +
                "##          ###########   ##      ##   ##########  ###########\n" +
                "##########  ###########   ##      ##    #########   #########\n" +
                "##########   ###### ###   ##      ##     #######     #######\n\n" +
                "######################################################################\n"
        );

        String[] serverIpAndPort = null;
        String[] clientIpAndPort = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println(
                "Which ones do you want to start?\n" +
                    "[1] Server\n" +
                    "[2] Client\n" +
                    "[3] Both\n"
        );

        boolean startServer = false;
        boolean startClient = false;

        switch (scanner.nextLine()) {
            case "1" -> startServer = true;
            case "2" -> startClient = true;
            case "3" -> {
                startServer = true;
                startClient = true;
            }
            default -> System.exit(1);
        }

        if (startServer) {
            System.out.println("Enter the ip and port for the server (Example: " +
                DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT + ")");

            String line = scanner.nextLine();

            if (line.isEmpty()) {
                serverIpAndPort = (DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT).split(":");
            } else {
                serverIpAndPort = line.split(":");
            }
        }

        if (startClient) {
            System.out.println("Enter the ip and port for the client (Example: " +
                DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT + ")");

            String line = scanner.nextLine();

            if (line.isEmpty()) {
                clientIpAndPort = (DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT).split(":");
            } else {
                clientIpAndPort = line.split(":");
            }
        }

        if (serverIpAndPort != null) {
            new LanceServer(serverIpAndPort[0], Integer.parseInt(serverIpAndPort[1]));
        }

        if (clientIpAndPort != null) {
            LanceClient client = new LanceClient(clientIpAndPort[0], Integer.parseInt(clientIpAndPort[1]));

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
}
