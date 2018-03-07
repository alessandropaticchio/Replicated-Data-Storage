package server.multicast;

import server.message.Ack;
import server.message.Join;
import server.message.Message;
import server.message.Write;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastHandler implements Runnable{

    //Address
    private InetAddress group;
    private final int port;
    private long clock;

    //Socket
    MulticastSocket s;
    private final int bufferSize = 1024 * 4; //Maximum size of transfer object


    public MulticastHandler(String groupAddress, int port) throws UnknownHostException {
        this.group = InetAddress.getByName(groupAddress);
        this.port = port;
        this.clock = 0;
    }

    public void connect() {
        try {
            //create socket
            s = new MulticastSocket(this.port);
            s.joinGroup(this.group);
            this.send(new Join(s.getInetAddress().toString() + ' ' + s.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(Message msg) throws IOException {
        // Increase clock
        if(msg instanceof Write) {
            this.clock += 1;
            msg.setClock(this.clock);
        }

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
