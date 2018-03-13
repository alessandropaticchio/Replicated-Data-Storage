package server;

import client.ClientMessage;
import client.ReadMessage;
import client.WriteMessage;
import com.oracle.tools.packager.Log;
import org.json.simple.parser.ParseException;
import server.logic.LogicHandler;
import server.logic.Record;

import java.io.*;
import java.net.*;
public class ClientConnection extends Thread{
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    ClientMessage message = new ClientMessage("0");
    private LogicHandler lh;

    public ClientConnection(Socket connection, LogicHandler lh) {
        this.connection = connection;
        this.lh = lh;
    }

    public void run()
    {
        try{

            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            out.writeObject("connection successful");
            out.flush();
            //4. The two parts communicate via the input and output streams
            do{
                try{
                    message = (ClientMessage)in.readObject();
                    System.out.println("message received for file: " + message.getDataID());
                    if (message.getDataID().equals("bye"))
                        sendMessage("bye");
                    else if (message instanceof WriteMessage) {
                        lh.writePrimitive(Integer.parseInt(message.getDataID()), ((WriteMessage) message).getValue(), connection);
                        sendMessage("file wrote");
                    }
                    else if (message instanceof ReadMessage) {
                        Record rec = lh.readPrimitive(Integer.parseInt(message.getDataID()));
                        if(rec.getID() == -1 && rec.getValue() == -1){
                            sendMessage("file not found");
                        } else {
                            sendMessage("file " + message.getDataID() + " has data: " + rec.getValue());
                        }
                    }
                }
                catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                } catch (ParseException e) {
                    e.printStackTrace();
                }catch (SocketException e) {
                    System.out.println("one client disconnected");
                    message.setDataID("bye");
                }
            }while (!message.getDataID().equals("bye")) ;
          }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
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
            System.out.println("server>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

}
