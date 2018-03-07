package server.message;

public class Join extends Message {

    public Join(String sender, String type, long clock) {
        super(sender, "join", clock);
    }

}
