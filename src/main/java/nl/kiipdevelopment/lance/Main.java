package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.client.LanceConsoleClient;
import nl.kiipdevelopment.lance.configuration.ConfigurationBuilder;
import nl.kiipdevelopment.lance.server.LanceServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        try {
            lanceClient.setFile("test", Files.readAllBytes(Path.of("build.gradle")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lanceClient.getFile("test");
        //end test
    }
}
