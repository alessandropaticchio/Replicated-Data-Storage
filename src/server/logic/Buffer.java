package server.logic;

import java.util.ArrayDeque;
import client.WriteMessage;
import client.ReadMessage;
import server.logic.BufferSlot;
import server.logic.Record;

public class Buffer extends ArrayDeque<BufferSlot> {

  private LogicHandler lh;
  private int writeCount = 0;

  public Buffer(LogicHandler lh) {
    super();
    this.lh = lh;
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
        lh.writePrimitive(temp.getMessage().getDataID(), ((WriteMessage) temp.getMessage()).getValue(), temp.getConnection() .toString());
      } else if(temp.getMessage() instanceof ReaMessage) {
        if(writeCount > 0)
          break;
        else {
          temp = this.poll();
          Record rec = lh.readPrimitive(temp.getMessage().getDataID());
          if(rec.getID() == -1 && rec.getValue() == -1){
              temp.reply("No file with this ID...");
          } else {
              temp.reply("File with ID " + message.getDataID() + " has data: " + rec.getValue());
          }
        }
      }
    }
  }

}
