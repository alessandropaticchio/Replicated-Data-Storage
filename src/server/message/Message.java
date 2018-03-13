package server.message;

import java.io.Serializable;

public class Message implements Serializable {

    private int senderID;
    private long clock;

    public Message(int senderID) {
        this.senderID = senderID;
    }

    public Message(int senderID, long clock) {
        this.senderID = senderID;
        this.clock = clock;
    }

    public int getSenderID() {
        return senderID;
    }


    public long getClock() {
        return clock;
    }

    public void setClock(long clock) {
        this.clock = clock;
    }
}
