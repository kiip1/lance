package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceConsoleClient;
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

        new LanceServer().start();
        new LanceConsoleClient().start();
    }
}
