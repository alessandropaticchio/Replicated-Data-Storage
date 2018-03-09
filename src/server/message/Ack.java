package server.message;

public class Ack  extends Message{

        private String origin; //the ip address of the sender of the message I want to acknowledge

    public Ack(String sender, String type, String origin, long clock) {
        super(sender, "ack", clock);
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

}
