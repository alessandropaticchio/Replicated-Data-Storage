package server.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class PersistenceHandler {
    private JSONParser parser = new JSONParser();

    public void persist(int id, int value) throws IOException, ParseException {
        JSONObject newData = new JSONObject();
        newData.put("ID", id);
        newData.put("VALUE", value);
        FileWriter dataStorage = new FileWriter("C:\\Users\\aless\\Desktop\\replicated-data-storage\\src\\server\\logic\\datastorage.txt");
        dataStorage.write(newData.toJSONString());
        dataStorage.flush();
        System.out.println("The object ID: " + id + ", VALUE: " + value + " is now persistent.");
    }

    public static void main(String[] args) throws IOException, ParseException {
        PersistenceHandler ph = new PersistenceHandler();
        ph.persist(2,3);
    }
}
