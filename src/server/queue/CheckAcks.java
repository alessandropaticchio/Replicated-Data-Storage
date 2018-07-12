package server.queue;

import server.message.Ack;
import server.multicast.GroupMember;
import server.multicast.MulticastHandler;

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

    @Override
    public void run() {

        if(this.queue.contains(queueSlot) && !queueSlot.isReady()) {
            ArrayList<GroupMember> missed = new ArrayList<>();
            for(GroupMember g : this.mh.getMembers().values()) {
                if(!findAck(g, queueSlot.getAcks()))
                    missed.add(g);
            }
            this.mh.resend(queueSlot, missed);
        }

    }

}
