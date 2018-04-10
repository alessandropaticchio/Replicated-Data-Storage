package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReceivingThread extends Thread {

    private Socket socket;
    private String message;
    private ObjectInputStream in;


    public ReceivingThread(Socket requestSocket) {
        this.socket = requestSocket;
    }

    public void run(){

        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        do{
            try {
                message = (String) in.readObject();
                System.out.println(message);
            } catch (IOException e) {
                return;
            } catch (ClassNotFoundException e) {
                return;
            };

        }while(true);

    }

}
