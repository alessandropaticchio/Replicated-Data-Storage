package server.retransmission;

import client.ClientMessage;
import client.ReadMessage;
import org.json.simple.parser.ParseException;
import server.Server;
import server.buffer.BufferSlot;
import server.queue.QueueSlot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerConnection extends Thread{

    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    ClientMessage message = new ClientMessage(0);
    private Server server;

    public ServerConnection(Socket connection, Server server) {
        this.connection = connection;
        System.out.println(connection.toString());
        this.server = server;
    }

    public void run()
    {
        try{

            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            server.getTcs().setOutputs(out);
            //4. The two parts communicate via the input and output streams
            QueueSlot slot = (QueueSlot) in.readObject();
            this.server.getQueue().retransmission(slot);
          }
        catch(IOException ioException){
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                server.getTcs().removeOutput(out);
                connection.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }


    void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

}
