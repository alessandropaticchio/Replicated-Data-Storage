package server.multicast;

import java.net.InetAddress;

public class GroupMember {

    private InetAddress address;
    private int port;
    private int ID;

    public GroupMember(InetAddress address, int port, int ID) {
        this.address = address;
        this.port = port;
        this.ID = ID;
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

    @Override
    public String toString() {
        return this.address.toString() + ' ' + this.port + ' ' + this.ID;
    }

}
