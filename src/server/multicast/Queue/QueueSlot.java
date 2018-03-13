package server.multicast.Queue;

import server.message.Ack;
import server.message.Message;

import java.net.InetAddress;
import java.util.ArrayList;

public class QueueSlot {

    private boolean ready;
    private Message message;
    private ArrayList<Ack> acks;
    private InetAddress address;
    private int port;

    public QueueSlot(Message message, InetAddress address, int port) {
        this.ready = false;
        this.message = message;
        this.acks = new ArrayList<>();
        this.address = address;
        this.port = port;
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

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void addAck(Ack ack) {
        this.acks.add(ack);
    }

}
