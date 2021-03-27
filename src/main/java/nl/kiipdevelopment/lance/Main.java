package nl.kiipdevelopment.lance;

import com.google.gson.JsonPrimitive;
import nl.kiipdevelopment.lance.client.LanceClient;
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

            serverIpAndPort = scanner.nextLine().split(":");
        }

        if (startClient) {
            System.out.println("Enter the ip and port for the client (Example: " + DefaultConfiguration.HOST + ":" + DefaultConfiguration.PORT + ")");

            clientIpAndPort = scanner.nextLine().split(":");
        }

        if (serverIpAndPort != null)
            new LanceServer(serverIpAndPort[0], Integer.parseInt(serverIpAndPort[1])).start();

        if (clientIpAndPort != null)
            new LanceConsoleClient(clientIpAndPort[0], Integer.parseInt(clientIpAndPort[1])).start();
        lanceClient.start();
    
        System.out.println(lanceClient.get("hello").getAsJson());
        System.out.println(lanceClient.get("hello.hoi").getAsJson());
    
        lanceClient.set("hello", new JsonPrimitive("HELLO"));
        
        //end test
    }
}
