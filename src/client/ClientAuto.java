package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;


public class ClientAuto {

    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    ReceivingThread receivingThread;
    final int port = 2004;

    ClientAuto(){}

    public static void main(String args[]) throws ClassNotFoundException {
        ClientAuto client = new ClientAuto();
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

            do {
                System.out.println("Type anything and press Enter to start...");
                String choice = scanner.next();

                Random rnd = new Random();

                //Communicating with the server
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(50);
                    int id = rnd.nextInt(5);
                    int value = rnd.nextInt(50);
                    if (Math.random() < 0.5) {
                        read(id);
                        System.out.println("AUTO-READ ID: " + id);
                    }
                    else {
                        write(id, value);
                        System.out.println("AUTO-WRITE ID: " + id + ", VALUE: " + value);
                    }
                }
            } while(true);
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        } catch(IOException ioException) {
            System.out.println("No server found at the given IP");
            this.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
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