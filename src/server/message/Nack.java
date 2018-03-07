package server.message;

public class Nack extends Message{

    public Nack(String sender, String type, long clock) {
        super(sender, "nack", clock);
    }

}
