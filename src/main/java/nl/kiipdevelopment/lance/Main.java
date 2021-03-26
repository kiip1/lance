package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceClient;
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
            """
        );

        String version = Main.class.getPackage().getImplementationVersion();

        if (version == null)
            version = "Test";

        version = " " + version + " ";

        String out = String
            .format("%" + 70 + "s%s%" + 70 + "s", "", version.replace(" ", "<SPACE>"), "")
            .replace(" ", "#")
            .replace("<SPACE>", " ");

        System.out.println(out.substring(out.length() / 2 - 35, out.length() / 2 + 35));

        new LanceServer().start();
        new LanceConsoleClient().start();

        // start of test
        LanceClient client = new LanceClient();

        client.start();

        System.out.println(client.getString("test"));
        // end of test
    }
}
