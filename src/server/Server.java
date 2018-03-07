package server;

import server.multicast.MulticastHandler;

import java.net.UnknownHostException;

public class Server {

    private final MulticastHandler multicast;
    private final String groupAddr = "222.222.222.222";
    private final int port = 9504;

    public Server() throws UnknownHostException {
        this.multicast = new MulticastHandler(groupAddr, port);
    }

    public void start() {
        // Run multicast handler
        new Thread(multicast).start();
    }

}
