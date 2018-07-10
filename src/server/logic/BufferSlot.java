package server.logic;

import client.ClientMessage;

public class BufferSlot {

  private ClientMessage message;
  private ObjectOutputStream out;
  private Socket connection;

  public BufferSlot(ClientMessage message, ObjectOutputStream out, Socket connection) {
    this.message = message;
    this.out = out;
    this.connection = connection;
  }

  public ClientMessage getMessage() { return this.message }
  public ObjectOutputStream getOut() { return this.out }
  public Socket getConnection() { return this.connection }

  public void reply(String string)
  {
      try {
          this.out.writeObject(string);
          this.out.flush();
      } catch(IOException ioException){
          ioException.printStackTrace();
      }
  }

}
