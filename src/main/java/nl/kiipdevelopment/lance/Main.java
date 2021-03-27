package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.client.LanceConsoleClient;
import nl.kiipdevelopment.lance.configuration.ConfigurationBuilder;
import nl.kiipdevelopment.lance.server.LanceServer;

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

        new LanceServer(new ConfigurationBuilder().setPassword("test").buildServerConfiguration()).start();

        //start test
        LanceClient lanceClient = new LanceConsoleClient(new ConfigurationBuilder().setPassword("test").build());

        lanceClient.start();

        lanceClient.setString("abc", "123");

        lanceClient.getString("abc");

        lanceClient.getInteger("abc");
        //end test
    }
}
