package server.queue;

import org.json.simple.parser.ParseException;
import server.Server;
import server.message.Ack;
import server.multicast.GroupMember;

import java.io.IOException;
import java.util.*;

public class InputQueue extends PriorityQueue<QueueSlot> {

    private Server server;

    public InputQueue(Server server) {
        // Sorting element according to the timestamp and/or ID
        super(new Comparator<QueueSlot>() {
            @Override
            public int compare(QueueSlot o1, QueueSlot o2) {
                if(o1.getMessage().getClock() == o2.getMessage().getClock()) {
                    if(o1.getAddress().equals(o2.getAddress())) {
                        if(o1.getPort() == o2.getPort())
                            return o1.getMessage().getSenderID() < o2.getMessage().getSenderID() ? -1 : 1;
                        else
                            return o1.getPort() < o2.getPort() ? -1 : 1;
                    } else
                        return o1.getAddress().toString().compareTo(o2.getAddress().toString());
                }
                else
                    return o1.getMessage().getClock() < o2.getMessage().getClock() ? -1 : 1;
            }
        });
        this.server = server;
    }

    public synchronized void addSlot(QueueSlot slot) throws IOException {
        this.add(slot);
        this.server.getMulticast().send(new Ack(this.server.getMulticast().getID(), slot.getMessage().getClock(), slot.getAddress(), slot.getPort(), slot.getMessage().getSenderID()));
    }

    public synchronized void addAck(Ack ack, HashMap<String, GroupMember> members) throws IOException, ParseException {
        // Search the correct slot to which add the ack
        Iterator<QueueSlot> slots = this.iterator();
        QueueSlot slot = null;
        boolean found = false;
        while(slots.hasNext() && !found) {
            slot = slots.next();
            if(slot.getAddress().equals(ack.getOriginAddr()) && slot.getPort() == ack.getOriginPort()
                    && slot.getMessage().getSenderID() == ack.getOriginID() && slot.getMessage().getClock() == ack.getClock())
                found = true;
        }
        // Add the ack
        if(found) {
            slot.addAck(ack);

            // Check if all the acks are arrived
            int i = 0;
            try {
                while (members.get(slot.getAcks().get(i).getOriginAddr().toString() + ' ' + slot.getAcks().get(i).getOriginPort()
                        + ' ' + slot.getAcks().get(i).getOriginID()) != null)
                    i += 1;
            } catch(IndexOutOfBoundsException e) {
                if (i == members.size())
                    slot.setReady(true);
            } finally {
                while(this.available()) {
                    QueueSlot drawnSlot = this.draw();
                    this.server.getLogic().write(drawnSlot);
                }
            }
        }
    }

    public boolean available() {
        // Is the header ready to be processed?
        if(this.peek() != null)
            return this.peek().isReady();
        else
            return false;
    }

    public QueueSlot draw() {
        return this.poll();
    }

}
