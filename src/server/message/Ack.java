package server.message;

import java.net.InetAddress;

public class Ack  extends Message{

    private InetAddress originAddr;
    private int originPort;
    private int originID;

    public Ack(int senderID, long clock, InetAddress originAddr, int originPort, int originID) {
        super(senderID, clock);
        this.originAddr = originAddr;
        this.originPort = originPort;
        this.originID = originID;
    }

    public InetAddress getOriginAddr() {
        return originAddr;
    }

    public int getOriginPort() {
        return originPort;
    }

    public int getOriginID() {
        return originID;
    }

    public String toString() {
        return "Addr: " + originAddr + ", Port: " + originPort + ", ID: " + originID;
    }

}
