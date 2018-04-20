package server;

import client.ClientMessage;
import client.ReadMessage;
import client.WriteMessage;
//import com.oracle.tools.packager.Log;
import org.json.simple.parser.ParseException;
import server.logic.LogicHandler;
import server.logic.Record;

import java.io.*;
import java.net.*;


public class ClientConnection extends Thread{

    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    ClientMessage message = new ClientMessage(0);
    private LogicHandler lh;
    private ThreadedClientServer tes;

    public ClientConnection(Socket connection, LogicHandler lh, ThreadedClientServer tes) {
        this.connection = connection;
        System.out.println(connection.toString());
        this.lh = lh;
        this.tes = tes;
    }

    public void run()
    {
        try{

            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            tes.setOutputs(out);
            //4. The two parts communicate via the input and output streams
            do{
                try{
                    message = (ClientMessage)in.readObject();
                    System.out.println("Write occurred on file: " + message.getDataID());
                    if (message.getDataID() == -1)
                        sendMessage("bye");
                    else if (message instanceof WriteMessage) {
                        lh.writePrimitive(message.getDataID(), ((WriteMessage) message).getValue(), connection.toString());
                    }
                    else if (message instanceof ReadMessage) {
                        Record rec = lh.readPrimitive(message.getDataID());
                        if(rec.getID() == -1 && rec.getValue() == -1){
                            sendMessage("No file with this ID...");
                        } else {
                            sendMessage("File with ID " + message.getDataID() + " has data: " + rec.getValue());
                        }
                    }
                }
                catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                } catch (ParseException e) {
                    e.printStackTrace();
                }catch (SocketException e) {
                    System.out.println("Client disconnected");
                    message.setDataID(-1);
                }
            }while (message.getDataID() != -1) ;
          }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                in.close();
                out.close();
                tes.removeOutput(out);
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
