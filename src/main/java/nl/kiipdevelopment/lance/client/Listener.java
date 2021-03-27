package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;

public interface Listener {
    boolean run(LanceMessage message);
}
