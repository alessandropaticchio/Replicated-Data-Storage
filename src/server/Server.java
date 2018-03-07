package server;

import server.message.Ack;
import server.message.Message;
import server.message.Write;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Server implements Runnable{

    //Address
    private InetAddress group;
    private final int port;

    //Socket
    MulticastSocket s;
    private final int bufferSize = 1024 * 4; //Maximum size of transfer object


    public Server(String groupAddress, int port) throws UnknownHostException {
        this.group = InetAddress.getByName(groupAddress);
        this.port = port;
    }

    public void connect() {
        try {
            //create socket
            s = new MulticastSocket(this.port);
            s.joinGroup(this.group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(Message msg) throws IOException {
        //Prepare Data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        byte[] data = baos.toByteArray();

        //Send data
        s.send(new DatagramPacket(data, data.length, group, port));
    }

    @Override
    public void run() {

        this.connect();

        //Receive data
        while (true) {

            //Create buffer
            byte[] buffer = new byte[bufferSize];
            try {
                s.receive(new DatagramPacket(buffer, bufferSize, group, port));
                //Deserialze object
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
                //Action associated to the message type
                if (readObject instanceof Write) {
                    Write message = (Write) readObject;
                } else if(readObject instanceof Ack) {
                    Ack message = (Ack) readObject;
                } else {
                    System.out.println("The received object is not of type String!");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
