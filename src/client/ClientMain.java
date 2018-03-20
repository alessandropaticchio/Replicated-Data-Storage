package client;

import java.io.*;
import java.net.*;
import java.util.*;


public class ClientMain {
    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    int flag = 0;
    ReceivingThread receivingThread;
    ClientMain(){}

    public static void main(String args[]) throws ClassNotFoundException {
        ClientMain client = new ClientMain();
        client.run();
    }

    void run() throws ClassNotFoundException {
        try{
            Scanner scanner = new Scanner(System.in);
            //creating a socket to connect to the server
            System.err.println("Welcome, insert the IP address of the desired server:");
            String ip = scanner.next();
            requestSocket = new Socket(ip, 2004);
            System.out.println("Connected to server: "+ ip + " in port 2004");
            //get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();

            new ReceivingThread(requestSocket).start();

            //Communicating with the server
            do{

                System.out.println("1:READ, 2:WRITE, Others inputs:DISCONNECT");

                String choice = scanner.next();
                switch (choice) {
                    case "1":
                        System.err.println("Insert the file ID: ");
                        String toSearchID = scanner.next();
                        read(Integer.parseInt(toSearchID));
                        break;
                    case "2" :
                        System.err.println("Insert the file ID: ");
                        String dataId = scanner.next();
                        System.err.println("Insert the new value: ");
                        String  newValue = scanner.next();
                        write(Integer.parseInt(dataId), Integer.parseInt(newValue));
                        break;
                    default:
                        ClientMessage msg = new ClientMessage(-1);
                        out.writeObject(msg);
                        flag=1;
                        System.out.println("You are disconnected");
                        break;
                }

            }while(flag==0);
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        } catch(IOException ioException) {
            System.out.println("No Server");
            this.run();
        } finally{
            //4: Closing connection
            try{
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }


    void write(int dataId, int newValue){
        WriteMessage msg = new WriteMessage(dataId, newValue);
        send(msg);
    }

    void read(int dataId){
        ReadMessage msg = new ReadMessage(dataId);
        send(msg);
    }

    void send(ClientMessage msg){
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("message sent");
        }
        catch(SocketException e){
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}