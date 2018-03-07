package server.multicast.Queue;

import server.message.Ack;
import server.message.Message;

import java.util.ArrayList;

public class QueueSlot {

    private boolean ready;
    private Message message;
    private ArrayList<Ack> acks;

    public QueueSlot(Message message) {
        this.ready = false;
        this.message = message;
        this.acks = new ArrayList<>();
    }

    public boolean isReady() {
        return ready;
    }

    public Message getMessage() {
        return message;
    }

    public ArrayList<Ack> getAcks() {
        return acks;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void addAck(Ack ack) {
        this.acks.add(ack);
    }

}
