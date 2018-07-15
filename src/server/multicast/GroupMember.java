package server.multicast;

import java.net.InetAddress;

public class GroupMember {

    private InetAddress address;
    private int port;
    private int ID;
    private SequenceNumber sequenceNumber;

    public GroupMember(InetAddress address, int port, int ID) {
        this.address = address;
        this.port = port;
        this.ID = ID;
        this.sequenceNumber = new SequenceNumber();
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getID() {
        return ID;
    }

    public SequenceNumber getSequenceNumber() { return sequenceNumber; }

    @Override
    public String toString() {
        return this.address.toString() + ' ' + this.port + ' ' + this.ID;
    }

}
