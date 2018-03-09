package server;

import java.io.IOException;
import java.net.*;

public class ThreadedEchoServer {

    static final int PORT = 2004;

    public static void main(String args[]) {
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
                System.out.println("Connection received from " + socket.getInetAddress().getHostName());

            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new ClientConnection(socket).start();
        }
    }
}