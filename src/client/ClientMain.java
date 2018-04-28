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
    final int port = 2004;

    ClientMain(){}

    public static void main(String args[]) throws ClassNotFoundException {
        ClientMain client = new ClientMain();
        client.run();
    }

    void run() throws ClassNotFoundException {
        try{
            Scanner scanner = new Scanner(System.in);
            //creating a socket to connect to the server
            System.out.println("Welcome to the Replicated Storage Service!\nPlease, insert the IP address of the server you would like to join:");
            String ip = scanner.next();
            requestSocket = new Socket(ip, port);
            System.out.println("You are now connected to the Server: "+ ip + " at port" + port);
            //get Input and Output streams
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();

            new ReceivingThread(requestSocket).start();

            //Communicating with the server
            do{

                System.out.println("1:READ, 2:WRITE, 3:DISCONNECT");

                String choice = scanner.next();
                switch (choice) {
                    case "1":
                        System.out.println("Insert the file ID: ");
                        String toSearchID = scanner.next();
                        read(Integer.parseInt(toSearchID));
                        break;
                    case "2" :
                        System.out.println("Insert the file ID: ");
                        String dataId = scanner.next();
                        System.out.println("Insert the new value: ");
                        String  newValue = scanner.next();
                        write(Integer.parseInt(dataId), Integer.parseInt(newValue));
                        break;
                    case "3":
                        ClientMessage msg = new ClientMessage(-1);
                        out.writeObject(msg);
                        flag=1;
                        System.out.println("You are now disconnected");
                        break;
                    default:
                        break;
                }

            }while(flag==0);
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        } catch(IOException ioException) {
            System.out.println("No server found at the given IP");
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
        }
        catch(SocketException e){
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}