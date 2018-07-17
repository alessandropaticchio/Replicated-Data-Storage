package server.buffer;

import java.io.IOException;
import java.util.ArrayDeque;

import client.ClientMessage;
import client.WriteMessage;
import client.ReadMessage;
import server.Server;
import server.logic.Record;
import server.message.Write;
import server.multicast.SequenceNumber;

public class Buffer extends ArrayDeque<BufferSlot> {

  private Server server;
  private int writeCount = 0;
  private SequenceNumber sn;

  public Buffer(Server server) {
    super();
    this.server = server;
    this.sn = new SequenceNumber();
  }

  public synchronized void addToBuffer(BufferSlot slot) {
    this.add(slot);
    this.trigger();
  }

  public synchronized void writeCompleted() {
    writeCount--;
    this.trigger();
  }

  private void trigger() {
    BufferSlot temp = this.poll();
    while(temp != null) {
      if(temp.getMessage() instanceof WriteMessage) {
        writeCount++;
        try {
          this.server.getMulticast().send(new Write(this.server.getID(), temp.getMessage().getDataID(), ((WriteMessage)temp.getMessage()).getValue(), this.sn.getSequenceNumber(), temp.getConnection().toString()));
          this.sn.incrementSequenceNumber();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        if (writeCount > 0) {
          this.addFirst(temp);
          break;
        }
        else {
          System.out.println(this.size());
          System.out.println(temp);
          this.server.getLogic().read(temp);
        }
      }
      temp = this.poll();
    }
  }

}
