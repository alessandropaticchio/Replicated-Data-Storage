package server.multicast.Queue;

import org.json.simple.parser.ParseException;
import server.Server;
import server.message.Write;

import java.io.IOException;

public class CheckAvailable implements Runnable {

    Server server;
    InputQueue queue;

    public CheckAvailable(Server server, InputQueue queue) {
        this.server = server;
        this.queue = queue;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(10);
                if (this.queue.available()) {
                    QueueSlot slot = this.queue.draw();
                    Write msg = (Write) slot.getMessage();
                    this.server.getLogic().fromQueue(msg.getFile(), msg.getData(), msg.getSocketString());
                }
            } catch (InterruptedException |ParseException |IOException e) {
                e.printStackTrace();
            }
        }
    }

}
