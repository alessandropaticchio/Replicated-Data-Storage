package server.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.Server;
import server.ThreadedClientServer;
import server.message.Message;
import server.message.Write;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogicHandler {
    private HashMap<Integer,Integer> volatileDataStorage;
    private final PersistenceHandler ph;
    private String fileName = "src\\server\\logic\\datastorage.txt" ;
    private final Server server;
    private ThreadedClientServer tes;
    private Lock queueAccessLock;

    public LogicHandler(Server server) {

        this.volatileDataStorage = new HashMap<Integer, Integer>();
        this.ph = new PersistenceHandler();
        this.server = server;
        this.queueAccessLock = new ReentrantLock();

    }

    public void fetchData() throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONObject dataStorage = (JSONObject) parser.parse(new FileReader(fileName));
        JSONArray dataArray = (JSONArray) dataStorage.get("Data");
        for (Object o : dataArray) {
            JSONObject jsonLineItem = (JSONObject) o;
            int idToAdd = Integer.parseInt(jsonLineItem.get("ID").toString());
            int valueToAdd = Integer.parseInt(jsonLineItem.get("VALUE").toString());
            this.volatileDataStorage.put(idToAdd,valueToAdd);
        }
    }

    public Record readPrimitive(int id){

        if (this.volatileDataStorage.containsKey(id)){
            Record toSend = new Record(id, volatileDataStorage.get(id));
            System.out.println("The record with ID: " + id + " has been found." +
                    "It has value: " + volatileDataStorage.get(id) + " and it is now sent to the client");
            return toSend;
        }
        else{
            System.out.println("The record with ID: " + id + " does not exist");
            return new Record(-1,-1); //invalid
        }

    }

    public synchronized void fromQueue(Message msg) throws IOException, ParseException {

        if(msg instanceof Write){
            Write msgWrite = (Write)msg;
            System.out.println("Server Op: fromQueue");

            this.volatileDataStorage.put(msgWrite.getFile(), msgWrite.getData());
            this.ph.persist(new Record(msgWrite.getFile(), msgWrite.getData()));

            tes = server.getTes();
            tes.sendConfirm("WRITE for file ID: " + msgWrite.getFile() + ", with value: " + msgWrite.getData() + " has been executed by socket: " + msgWrite.getSocketString());
        }

    }

    public synchronized void writePrimitive(int id, int value, String socketString) {
        try {
            this.server.toQueue(id, value, socketString);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        System.out.println("Write message with ID: " + id + " and VALUE: " + value + " is sent to the replicated storages by socket: " + socketString);
    }


}
