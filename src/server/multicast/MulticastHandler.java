package server.multicast;

import org.json.simple.parser.ParseException;
import server.GetIP;
import server.Server;
import server.message.*;
import server.multicast.Queue.InputQueue;
import server.multicast.Queue.QueueSlot;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;


public class MulticastHandler implements Runnable{

    //Address
    private InetAddress group;
    private final int port;
    private long clock;
    private int ID;

    //Socket
    MulticastSocket s;
    private final int bufferSize = 1024 * 4; //Maximum size of transfer object

    //Queue
    HashMap<String, GroupMember> members;
    InputQueue queue;

    Server server;

    public MulticastHandler(String groupAddress, int port, int ID, Server server) throws UnknownHostException {
        this.group = InetAddress.getByName(groupAddress);
        this.port = port;
        this.clock = 0;
        this.ID = ID;
        this.members = new HashMap<>();
        this.queue = new InputQueue();
        this.server = server;
    }


    public void connect() {
        try {
            //create socket
            s = new MulticastSocket(this.port);
            s.setInterface(GetIP.getIP());
            s.joinGroup(this.group);
            this.send(new Join(this.ID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(Message msg) throws IOException {
        // Increase clock
        if(!(msg instanceof Ack || msg instanceof Nack)) {
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
            DatagramPacket datagram = new DatagramPacket(buffer, bufferSize);
            try {
                s.receive(datagram);
                //Deserialze object
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
                System.out.println(readObject.toString());
                System.out.println(datagram.getAddress());
                //Action associated to the message type
                if (readObject instanceof Write) {
                    Write message = (Write) readObject;
                    this.clock = Long.max(this.clock, message.getClock()) + 1;
                    this.queue.add(new QueueSlot(message, datagram.getAddress(), datagram.getPort()));
                    this.send(new Ack(this.ID, message.getClock(), datagram.getAddress(), datagram.getPort(), message.getSenderID()));
                } else if(readObject instanceof Ack) {
                    Ack message = (Ack) readObject;
                    this.queue.addAck(message, (HashMap<String,GroupMember>)this.members.clone());
                    if(this.queue.available()) {
                        QueueSlot slot = this.queue.draw();
                        Write msg = (Write)slot.getMessage();
                        this.server.getLogic().fromQueue(msg.getFile(), msg.getData(), msg.getSocketString());
                    }
                } else if(readObject instanceof Join) {
                    Join message = (Join) readObject;
                    GroupMember member = new GroupMember(datagram.getAddress(), datagram.getPort(), message.getSenderID());
                    this.members.put(member.toString(), member);
                    this.send(new AckJoin(this.ID));
                } else if(readObject instanceof AckJoin) {
                    AckJoin message = (AckJoin) readObject;
                    GroupMember member = new GroupMember(datagram.getAddress(), datagram.getPort(), message.getSenderID());
                    this.members.put(member.toString(), member);
                    System.out.println(this.members);
                } else {
                    System.out.println("The received object is not of type String!");
                }
            } catch (IOException | ClassNotFoundException | ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
