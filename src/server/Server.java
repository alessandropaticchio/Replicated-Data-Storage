package server;

import server.multicast.MulticastHandler;

import java.net.UnknownHostException;
import java.util.Random;

public class Server {

    private final MulticastHandler multicast;
    private final String groupAddr = "225.4.5.6";
    private final int port = 44500;
    private final int ID;

    public Server() throws UnknownHostException {
        this.ID = new Random().nextInt(65000);
        this.multicast = new MulticastHandler(groupAddr, port, ID);
    }

    public void start() {
        // Run multicast handler
        new Thread(multicast).start();
    }

    public static void main(String[] args) throws UnknownHostException {
        Server server = new Server();
        server.start();
    }

}
