package server.queue;

import org.json.simple.parser.ParseException;
import server.message.Ack;
import server.multicast.GroupMember;
import server.multicast.MulticastHandler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class CheckAcks implements Runnable {

    private MulticastHandler mh;
    private QueueSlot queueSlot;
    private InputQueue queue;

    public CheckAcks(MulticastHandler mh, QueueSlot queueSlot, InputQueue queue) {
        this.mh = mh;
        this.queueSlot = queueSlot;
        this.queue = queue;
    }

    private boolean findAck(GroupMember g, ArrayList<Ack> acks) {
        boolean found = false;
        Iterator<Ack> iteracks = acks.iterator();
        Ack tempAck = null;
        while(!found && iteracks.hasNext()) {
            tempAck = iteracks.next();
            if(tempAck.getOriginAddr().equals(g.getAddress()) && tempAck.getOriginPort() == g.getPort())
                found = true;
        }
        return found;
    }

    private void sendRetransmission(QueueSlot queueSlot, InetAddress ip){

        ObjectOutputStream out = null;
        Socket requestSocket = null;
        try {
            requestSocket = new Socket(ip, 2005);
            System.out.println("You are now connected to the Server: "+ ip + " at port " + 2005);
            //get Input and Output streams

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeObject(queueSlot);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                requestSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void run() {

        if(this.queue.contains(queueSlot) && !queueSlot.isReady()) {
            ArrayList<GroupMember> missed = new ArrayList<>();
            for(GroupMember g : this.mh.getMembers().values()) {
                if(!findAck(g, queueSlot.getAcks()))
                    sendRetransmission(queueSlot, g.getAddress());
            }
        }

    }

}
