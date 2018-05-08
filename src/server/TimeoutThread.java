package server;

import server.message.Ack;
import server.message.Write;
import server.multicast.GroupMember;
import server.multicast.MulticastHandler;
import server.multicast.Queue.InputQueue;
import server.multicast.Queue.QueueSlot;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class TimeoutThread extends Thread {
    private QueueSlot queueSlot;
    private MulticastHandler multicastHandler;

    public TimeoutThread(QueueSlot queueSlot, MulticastHandler multicastHandler) {
        this.queueSlot = queueSlot;
        this.multicastHandler = multicastHandler;
    }

    @Override
    public void run() {
        try {
            sleep(100);
            if (queueSlot.isReady()) {
                System.out.println("All acks received, everything went fine!");
                return;
            } else {
                for (GroupMember gm : multicastHandler.getMembers().values()
                        ) {
                    boolean flag = false;
                    for (Ack a : queueSlot.getAcks()) {
                        if (a.getOriginAddr() == gm.getAddress()) {
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        System.out.println("Some acks lost, going to resend");
                        multicastHandler.send(queueSlot.getMessage());
                    }
                    flag = false;
                }
                this.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
