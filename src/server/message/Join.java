package server.message;

public class Join extends Message {

    public Join(String sender) {
        super(sender, "join");
    }

}
