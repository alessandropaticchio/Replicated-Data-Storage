package server.message;

import java.io.Serializable;

public class Message implements Serializable {

    private String sender;
    private String type;
    private long clock;

    public Message(String sender, String type, long clock) {
        this.sender = sender;
        this.type = type;
        this.clock = clock;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public long getClock() {
        return clock;
    }

}
