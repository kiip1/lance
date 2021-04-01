package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.StatusCode;

import java.io.PrintWriter;
import java.util.function.Function;

public class ResolvableListener<T> implements Listener {
    volatile LanceMessage value = null;
    
    private final int id;
    private final PrintWriter out;
    private final Function<LanceMessage, Boolean> filter;
    private final Function<LanceMessage, T> resolver;

    public ResolvableListener(int id, PrintWriter out, Function<LanceMessage, T> resolver) {
        this.id = id;
        this.out = out;
        this.filter = lanceMessage -> lanceMessage.getId() == id;
        this.resolver = resolver;
    }

    public ResolvableListener(int id, PrintWriter out, Function<LanceMessage, Boolean> filter, Function<LanceMessage, T> resolver) {
        this.id = id;
        this.out = out;
        this.filter = filter;
        this.resolver = resolver;
    }

    public T resolve(LanceClient client) throws ErrorStatusException {
        while (value == null)
            Thread.onSpinWait();
    
        client.setLastStatus(value.getCode());
        
        if (value.getCode() != StatusCode.OK) {
            throw new ErrorStatusException(value.getCode());
        }
        
        return resolver.apply(value);
    }
    
    @Override
    public boolean run(LanceMessage lanceMessage) {
        if (lanceMessage == null) {
            out.close();
            
            return false;
        }
        
        if (filter.apply(lanceMessage)) {
            value = lanceMessage;
            
            return true;
        }
        
        return false;
    }
}
