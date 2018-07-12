package server.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.GetIP;
import server.Server;
import server.ThreadedClientServer;
import server.buffer.BufferSlot;
import server.message.Message;
import server.message.Write;
import server.queue.QueueSlot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogicHandler {
    private HashMap<Integer,Integer> volatileDataStorage;
    private final PersistenceHandler ph;
    private String fileName = "src\\server\\logic\\datastorage.txt" ;
    private final Server server;
    private ReadWriteLock rwl;


    public LogicHandler(Server server) {

        this.volatileDataStorage = new HashMap<Integer, Integer>();
        this.ph = new PersistenceHandler();
        this.server = server;
        this.rwl = new ReentrantReadWriteLock();

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

    public void read(BufferSlot slot){

        Integer id = slot.getMessage().getDataID();

        rwl.readLock().lock();
        if (this.volatileDataStorage.containsKey(id)){
            Integer value = volatileDataStorage.get(id);
            rwl.readLock().unlock();
            System.out.println("The record with ID: " + id + " has been found." +
                    "It has value: " + volatileDataStorage.get(id) + " and it is now sent to the client");
            slot.reply("File with ID " + slot.getMessage().getDataID() + " has data: " + value);
        } else {
            rwl.readLock().unlock();
            System.out.println("The record with ID: " + id + " does not exist");
            slot.reply("No file with this ID...");
        }

    }

    public void write(QueueSlot slot) throws IOException, ParseException {

        Write msgWrite = ((Write)slot.getMessage());

        rwl.writeLock().lock();
        this.volatileDataStorage.put(msgWrite.getFile(), msgWrite.getData());
        this.ph.persist(new Record(msgWrite.getFile(), msgWrite.getData()));
        rwl.writeLock().unlock();

        this.server.getTes().sendConfirm("WRITE for file ID: " + msgWrite.getFile() + ", with value: " + msgWrite.getData() + " has been executed by socket: " + msgWrite.getSocketString());
        System.out.println("Write message with ID: " + msgWrite.getFile() + " and VALUE: " + msgWrite.getData() + " is sent to the replicated storages by socket: " + msgWrite.getSocketString());

        if(slot.getAddress().toString().equals(GetIP.getIP().toString()))
            this.server.getBuffer().writeCompleted();

    }

}
