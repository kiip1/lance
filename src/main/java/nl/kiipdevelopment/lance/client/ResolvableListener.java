package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;

import java.io.PrintWriter;
import java.util.function.Function;

public class ResolvableListener<T> implements Listener {
    volatile LanceMessage value = null;
    
    private final int id;
    private final PrintWriter out;
    private final Function<LanceMessage, T> resolver;
    
    public ResolvableListener(int id, PrintWriter out, Function<LanceMessage, T> resolver) {
        this.id = id;
        this.out = out;
        this.resolver = resolver;
    }
    
    public T resolve() {
        while (value == null)
            Thread.onSpinWait();
    
        return resolver.apply(value);
    }
    
    @Override
    public boolean run(String line) {
        LanceMessage lanceMessage = LanceMessage.getFromString(line);
        
        if (lanceMessage == null) {
            out.close();
            
            return false;
        }
        
        if (lanceMessage.getId() == id) {
            value = lanceMessage;
            
            return true;
        }
        
        return false;
    }
}
