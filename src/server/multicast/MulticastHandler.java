package server.multicast;

import org.json.simple.parser.ParseException;
import server.GetIP;
import server.Server;
import server.message.*;
import server.queue.CheckAcks;
import server.queue.QueueSlot;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MulticastHandler implements Runnable{

    //Address
    private InetAddress group;
    private final int port;
    private long clock;
    private int ID;

    //Socket
    MulticastSocket s;
    private final int bufferSize = 1024 * 4; //Maximum size of transfer object

    //queue
    HashMap<String, GroupMember> members;
    Server server;
    ScheduledThreadPoolExecutor executor;

    public MulticastHandler(String groupAddress, int port, int ID, Server server) throws UnknownHostException {
        this.group = InetAddress.getByName(groupAddress);
        this.port = port;
        this.clock = 0;
        this.ID = ID;
        this.members = new HashMap<>();
        this.server = server;
        this.executor = new ScheduledThreadPoolExecutor(1000);
    }

    public int getPort() { return port; }

    public long getClock() { return clock; }

    public int getID() { return ID; }

    public MulticastSocket getS() { return s; }

    public HashMap<String, GroupMember> getMembers() { return members; }

    public GroupMember getMember(InetAddress address, int port, int ID) {
        return this.members.get(address.toString() + ' ' + port + ' ' + ID);
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

    public void send(Message msg) throws IOException {
        // Increase clock
        if(msg instanceof Write || msg instanceof Join) {
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

    public void resend(QueueSlot slot, ArrayList<GroupMember> missed) {
        //TODO
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
                //Deserialize object
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
                System.out.println("From " + datagram.getAddress() + " >> " + readObject.toString());
                //Action associated to the message type
                if (readObject instanceof Write) {
                    Write message = (Write) readObject;
                    if(!datagram.getAddress().toString().equals(GetIP.getIP().toString()))
                        this.clock = Long.max(this.clock, message.getClock()) + 1;
                    QueueSlot newQueueSlot = new QueueSlot(message, datagram.getAddress(), datagram.getPort());
                    this.server.getQueue().addSlot(newQueueSlot);
                    executor.schedule(new CheckAcks(this, newQueueSlot, this.server.getQueue()), 5, TimeUnit.MILLISECONDS);
                } else if(readObject instanceof Ack) {
                    Ack message = (Ack) readObject;
                    this.server.getQueue().addAck(message, (HashMap<String,GroupMember>)this.members.clone());
                } else if(readObject instanceof Join) {
                    Join message = (Join) readObject;
                    GroupMember member = new GroupMember(datagram.getAddress(), datagram.getPort(), message.getSenderID());
                    this.members.put(member.toString(), member);
                    this.send(new AckJoin(this.ID));
                } else if(readObject instanceof AckJoin) {
                    AckJoin message = (AckJoin) readObject;
                    GroupMember member = new GroupMember(datagram.getAddress(), datagram.getPort(), message.getSenderID());
                    if(this.members.get(member.toString()) == null)
                        this.members.put(member.toString(), member);
                    System.out.println("Servers in the Multicast Group:\n" + this.members);

                } else {
                    System.out.println("The received object is not of type String!");
                }
            } catch (IOException | ClassNotFoundException | ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
