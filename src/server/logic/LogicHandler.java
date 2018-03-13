package server.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.Server;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LogicHandler {
    private HashMap<Integer,Integer> volatileDataStorage;
    private final PersistenceHandler ph;
    private String fileName = "src\\server\\logic\\datastorage.txt" ;
    private final Server server;

    public LogicHandler(Server server) {
        this.volatileDataStorage = new HashMap<Integer, Integer>();
        this.ph = new PersistenceHandler();
        this.server = server;
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

    public void writePrimitive(int id, int value) throws IOException, ParseException {
        this.volatileDataStorage.put(id,value);
        this.ph.persist(new Record(id,value));
    }

}
