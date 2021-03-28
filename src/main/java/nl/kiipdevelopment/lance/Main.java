package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceConsoleClient;
import nl.kiipdevelopment.lance.configuration.DefaultConfiguration;
import nl.kiipdevelopment.lance.server.LanceServer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println(
            """
                ##
                ##
                ##           ########     ## ######      #######     #######
                ##          ##########    ##########    #########   #########
                ##          ##########    ##########   ##########  ###########
                ##          ###    ###    ###    ###   ###    ###  ###     ###
                ##                 ###    ###    ###   ###         ###     ###
                ##             #######    ###    ###   ###         ###########
                ##           #########    ##      ##   ###         ###########
                ##          ##########    ##      ##   ###         ###
                ##          ###    ####   ##      ##   ###         ###
                ##          ###    ####   ##      ##   ###    ###  ####    ###
                ##          ###########   ##      ##   ##########  ###########
                ##########  ###########   ##      ##    #########   #########
                ##########   ###### ###   ##      ##     #######     #######
                
            ######################################################################
            """
        );

        String[] serverIpAndPort = null;
        String[] clientIpAndPort = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println(
            """
            Which ones do you want to start?
            
            [1] Server
            [2] Client
            [3] Both
            """
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
            System.out.println("Enter the ip and port for the server (Example: " + DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT + ")");

            String line = scanner.nextLine();

            if (line.isEmpty())
                serverIpAndPort = (DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT).split(":");
            else serverIpAndPort = line.split(":");
        }

        if (startClient) {
            System.out.println("Enter the ip and port for the client (Example: " + DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT + ")");

            String line = scanner.nextLine();

            if (line.isEmpty())
                clientIpAndPort = (DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT).split(":");
            else clientIpAndPort = line.split(":");
        }

        if (serverIpAndPort != null)
            new LanceServer(serverIpAndPort[0], Integer.parseInt(serverIpAndPort[1])).start();

        if (clientIpAndPort != null)
            new LanceConsoleClient(clientIpAndPort[0], Integer.parseInt(clientIpAndPort[1])).start();
    }
}
