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
    
            System.out.println("[" + getName() + "] " + "Accepted connection from " + ipAndPort + ".");
            server.connections.add(ipAndPort);
    
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
            boolean authorised = true;
    
            if (configuration.isPasswordEnabled()) {
                authorised = false;
        
                int id = ThreadLocalRandom.current().nextInt();
        
                out.println(new LanceMessage(
                        id,
                        StatusCode.AUTH_REQUIRED
                ));
        
                LanceMessage lanceMessage = LanceMessage.getFromString(in.readLine());
        
                if (lanceMessage == null) {
                    close(StatusCode.INVALID_MESSAGE);
            
                    return;
                }
        
                if (lanceMessage.getMessage().equals(configuration.getPassword())) {
                    authorised = true;
            
                    out.println(new LanceMessage(
                            id,
                            StatusCode.ACCESS_GRANTED
                    ));
                } else close(StatusCode.INTERNAL_ERROR);
            }
    
            if (authorised) while (active) {
                LanceMessage lanceMessage = LanceMessage.getFromString(in.readLine());
        
                if (lanceMessage == null) {
                    close(StatusCode.INVALID_MESSAGE);
            
                    return;
                }
        
                try {
                    out.println(CommandManager.handle(this, lanceMessage.getId(), lanceMessage).toString());
                } catch (Exception e) {
                    close(StatusCode.INTERNAL_ERROR);
                }
            }
    
            System.out.println("[" + getName() + "] " + "Closed connection from " + ipAndPort + ".");
            server.connections.remove(ipAndPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(StatusCode reason) {
        close(reason, ThreadLocalRandom.current().nextInt());
    }

    public void close(StatusCode reason, int id) {
        active = false;

        out.println(new LanceMessage(
            id,
            reason
        ));

        out.close();
    }
}
