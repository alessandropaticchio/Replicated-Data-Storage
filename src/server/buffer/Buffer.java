package server.buffer;

import java.io.IOException;
import java.util.ArrayDeque;

import client.ClientMessage;
import client.WriteMessage;
import client.ReadMessage;
import server.Server;
import server.logic.Record;
import server.message.Write;

public class Buffer extends ArrayDeque<BufferSlot> {

  private Server server;
  private int writeCount = 0;

  public Buffer(Server server) {
    super();
    this.server = server;
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
    BufferSlot temp = this.peek();
    while(temp != null) {
      if(temp.getMessage() instanceof WriteMessage) {
        writeCount++;
        temp = this.poll();
        try {
          this.server.getMulticast().send(new Write(this.server.getID(), temp.getMessage().getDataID(), ((WriteMessage)temp.getMessage()).getValue(), temp.getConnection().toString()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if(temp.getMessage() instanceof ReadMessage) {
        if(writeCount > 0)
          break;
        else {
          temp = this.poll();
          this.server.getLogic().read(temp);
        }
      }
    }
  }

}
