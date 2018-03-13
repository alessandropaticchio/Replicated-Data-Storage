package server.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LogicHandler {
    private HashMap<Integer,Integer> volatileDataStorage;
    private PersistenceHandler ph;
    private String fileName = "src\\server\\logic\\datastorage.txt" ;

    public LogicHandler() {
        this.volatileDataStorage = new HashMap<Integer, Integer>();
        this.ph = new PersistenceHandler();
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

    public static void main(String[] args) throws IOException, ParseException {
        LogicHandler lh = new LogicHandler();
        lh.fetchData();
        System.out.println(lh.volatileDataStorage.toString());
        lh.readPrimitive(2);
        lh.readPrimitive(5);
        lh.readPrimitive(66);
        lh.writePrimitive(2,6);
        System.out.println(lh.volatileDataStorage.toString());
    }
}
