package server;

import client.ClientMessage;
import client.ReadMessage;
import client.WriteMessage;

import java.io.*;
import java.net.*;
public class ClientConnection extends Thread{
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    ClientMessage message;

    public ClientConnection(Socket connection) {
        this.connection = connection;
    }

    public void run()
    {
        try{

            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            out.writeObject("connection successful");
            out.flush();
            //4. The two parts communicate via the input and output streams
            do{
                try{
                    message = (ClientMessage)in.readObject();
                    System.out.println("message received for file: " + message.getDataID());
                    if (message.getDataID().equals("bye"))
                        sendMessage("bye");
                    else if (message instanceof WriteMessage)
                        sendMessage("file wrote");
                    else if (message instanceof ReadMessage)
                        sendMessage("file " + message.getDataID() + " has data: xxx"); //xxx: answer with hashmap value!
                }
                catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                }
            }while(!message.getDataID().equals("bye"));
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                connection.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
    void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

}
