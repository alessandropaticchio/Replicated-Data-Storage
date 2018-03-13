package server;

import org.json.simple.parser.ParseException;
import server.logic.LogicHandler;
import server.logic.PersistenceHandler;

import java.io.IOException;
import java.net.InetAddress;

public class ServerMain {
    public static void main(String args[]) throws IOException, ParseException {

        System.out.println(InetAddress.getLocalHost().getHostAddress());

        LogicHandler lh = new LogicHandler();
        lh.fetchData();
        ThreadedEchoServer tes = new ThreadedEchoServer(2004, lh);
        tes.run();

    }
}
