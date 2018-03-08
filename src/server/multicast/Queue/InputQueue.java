package server.multicast.Queue;

import server.message.Ack;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class InputQueue extends PriorityQueue<QueueSlot> {

    public InputQueue() {
        super(new Comparator<QueueSlot>() {
            @Override
            public int compare(QueueSlot o1, QueueSlot o2) {
                if ( o1.getMessage().getSender().equals(o2.getMessage().getSender()))
                    return (o1.getMessage().getClock() < o2.getMessage().getClock()) ? -1 : 1;
                else
                    return o1.getMessage().getSender().compareTo(o2.getMessage().getSender());
            }
        });
    }

    public QueueSlot addAck(Ack ack) {
        // Search the correct slot to which add the ack
        Iterator<QueueSlot> slots = this.iterator();
        QueueSlot slot = null;
        boolean found = false;
        while(slots.hasNext() && !found) {
            slot = slots.next();
            if(slot.getMessage().getSender().equals(ack.getOrigin()) && slot.getMessage().getClock() == ack.getClock())
                found = true;
        }
        // Add the ack
        if(found) {
            slot.addAck(ack);
            return slot;
        } else
            return null;
    }
}
