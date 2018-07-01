package server.multicast;

import server.message.Ack;
import server.multicast.Queue.QueueSlot;

import java.util.ArrayList;
import java.util.Iterator;

public class CheckAcks implements Runnable {

    private MulticastHandler mh;
    private QueueSlot queueSlot;

    public CheckAcks(MulticastHandler mh, QueueSlot queueSlot) {
        this.mh = mh;
        this.queueSlot = queueSlot;
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

        if(this.mh.queue.contains(queueSlot) && !queueSlot.isReady()) {
            ArrayList<GroupMember> missed = new ArrayList<>();
            for(GroupMember g : this.mh.members.values()) {
                if(!findAck(g, queueSlot.getAcks()))
                    missed.add(g);
            }
            this.mh.resend(queueSlot, missed);
        }

    }

}
