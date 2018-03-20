package server;

import server.logic.LogicHandler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;

public class ThreadedClientServer {

    private static int PORT;
    private static LogicHandler lh;
    private ArrayList<ObjectOutputStream> outputs = new ArrayList<ObjectOutputStream>();

    public ThreadedClientServer(int PORT, LogicHandler lh) {
        this.PORT = PORT;
        this.lh = lh;
    }

    public void run(){
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
            ClientConnection c = new ClientConnection(socket, lh, this);
            c.start();
        }
    }

    public synchronized void sendConfirm(String msg){
        int i;
        for(i=0; i<outputs.size(); i++){
            try{
                outputs.get(i).writeObject(msg);
                outputs.get(i).flush();
                System.out.println("server>" + msg);
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    public void setOutputs(ObjectOutputStream output) {
        this.outputs.add(output);
    }
}