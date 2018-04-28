package server.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;

public class PersistenceHandler {
    private JSONParser parser = new JSONParser();
    private String fileName = "src\\server\\logic\\datastorage.txt" ;


    public void persist(Record record) throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONObject dataStorage = (JSONObject) parser.parse(new FileReader(fileName));
        JSONArray dataArray = (JSONArray) dataStorage.get("Data");
        boolean alreadyPresent = false;
        for (Object o : dataArray) {
            JSONObject jsonLineItem = (JSONObject) o;
            if (record.getID() == Integer.parseInt(jsonLineItem.get("ID").toString()))
            {
                jsonLineItem.put("VALUE", record.getValue());
                alreadyPresent = true;
                File newDataFile = new File(fileName);
                newDataFile.createNewFile();
                FileWriter filewriter = new FileWriter(newDataFile);
                filewriter.write(dataStorage.toJSONString());
                filewriter.flush();
                filewriter.close();
                System.out.println("The record with ID " + record.getID() + " was already present in our data storage. It has been overwritten with VALUE " + record.getValue() );
            }
        }
        if (!alreadyPresent) {
            JSONObject newData = new JSONObject();
            newData.put("ID", record.getID());
            newData.put("VALUE", record.getValue());
            dataArray.add(newData);
            dataStorage.put("Data", dataArray);
            File newDataFile = new File(fileName);
            newDataFile.createNewFile();
            FileWriter filewriter = new FileWriter(newDataFile);
            filewriter.write(dataStorage.toJSONString());
            filewriter.flush();
            filewriter.close();
            System.out.println("The record with ID: " + record.getID() + ", VALUE: " + record.getValue() + " is now persistent.");
        }
    }

}
