package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.client.LanceConsoleClient;
import nl.kiipdevelopment.lance.server.LanceServer;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
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

        new LanceServer().start();
        new LanceConsoleClient().start();

        // start of test
        LanceClient client = new LanceClient();

        client.start();

        System.out.println(client.getStringAsync("test").get());
        // end of test
    }
}
