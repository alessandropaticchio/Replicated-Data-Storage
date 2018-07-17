package server.retransmission;

import server.retransmission.ServerConnection;
import server.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedServerServer {

    private static int PORT;
    private static Server server;
    private ArrayList<ObjectOutputStream> outputs = new ArrayList<ObjectOutputStream>();

    public ThreadedServerServer(int PORT, Server server) {
        this.PORT = PORT;
        this.server = server;
    }

    public void run(){

        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.exit(0);
        }

        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("A new client is connected with IP " + socket.getInetAddress());

            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            ServerConnection c = new ServerConnection(socket, server);
            c.start();
        }
    }

    public void sendConfirm(String msg){

        System.out.println("Server Op: sendConfirm");
        for(ObjectOutputStream o:outputs){
            try{
                o.writeObject(msg);
                o.flush();
            } catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }

    public void setOutputs(ObjectOutputStream output) {
        this.outputs.add(output);
        System.out.println("Number of clients connected to this server: " + outputs.size());
    }

    public void removeOutput(ObjectOutputStream output) {
        this.outputs.remove(output);
    }

}