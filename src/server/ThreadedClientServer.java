package server;

import server.logic.LogicHandler;

import java.io.IOException;
import java.net.*;

public class ThreadedClientServer {

    private static int PORT;
    private static LogicHandler lh;

    public ThreadedClientServer(int PORT, LogicHandler lh) {
        this.PORT = PORT;
        this.lh = lh;
    }

    public static void run(){
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                System.out.println("Waiting for connection");
                socket = serverSocket.accept();
                System.out.println("Connection received from " + socket.getInetAddress());

            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new ClientConnection(socket, lh).start();
        }
    }
}