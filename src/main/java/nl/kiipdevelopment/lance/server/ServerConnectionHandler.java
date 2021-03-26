package nl.kiipdevelopment.lance.server;

import nl.kiipdevelopment.lance.configuration.ServerConfiguration;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.command.CommandManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class ServerConnectionHandler extends Thread {
    public final LanceServer server;
    public final ServerConfiguration configuration;
    public final Socket socket;

    private PrintWriter out;
    private BufferedReader in;

    private boolean active = true;

    public ServerConnectionHandler(LanceServer server, ServerConfiguration configuration, Socket socket) {
        super("Lance-Server-Connection-Handler");

        this.server = server;
        this.configuration = configuration;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String ipAndPort = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

            System.out.println("Accepted connection from " + ipAndPort + ".");
            server.connections.add(ipAndPort);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (active) {
                LanceMessage lanceMessage = LanceMessage.getFromString(in.readLine());

                if (lanceMessage == null) {
                    close("Invalid message.");

                    return;
                }

                out.println(CommandManager.handle(this, lanceMessage.getId(), lanceMessage).toString());
            }

            System.out.println("Closed connection from " + ipAndPort + ".");
            server.connections.remove(ipAndPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(String reason) {
        active = false;

        out.println(new LanceMessage(
            ThreadLocalRandom.current().nextInt(),
            StatusCode.CLOSING,
            reason
        ));

        out.close();
    }
}
