package server;

import org.json.simple.parser.ParseException;
import server.logic.LogicHandler;
import server.message.Write;
import server.multicast.MulticastHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Server {

    private final MulticastHandler multicast;
    private final LogicHandler logic;
    private final String groupAddr = "225.4.5.6";
    private final int port = 44500;
    private final int ID;
    private ThreadedClientServer tes;

    public Server() throws UnknownHostException {
        this.ID = new Random().nextInt(65000);
        this.multicast = new MulticastHandler(groupAddr, port, ID, this);
        this.logic = new LogicHandler(this);
    }

    public void start() throws IOException, ParseException {
        // Run multicast handler
        new Thread(multicast).start();
        this.logic.fetchData();
    }


    public static void main(String[] args) throws IOException, ParseException {
        Server server = new Server();
        server.start();

        System.out.println("Replicated Storage Service is on.\nThe IP of this server is: " + GetIP.getIP().toString());

        server.goTes(2004, server.getLogic());
    }

    public MulticastHandler getMulticast() {
        return multicast;
    }

    public LogicHandler getLogic() {
        return logic;
    }

    public void toQueue(int id, int data, String socketString) throws IOException {
        Write toSend = new Write(this.ID, id, data, socketString);
        this.multicast.send(toSend);

    }

    public ThreadedClientServer getTes() {
        return tes;
    }

    public void goTes(int port,LogicHandler lh) {
        this.tes = new ThreadedClientServer(port, lh);
        tes.run();
    }

}
