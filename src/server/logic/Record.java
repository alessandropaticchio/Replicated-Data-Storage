package server.logic;

import java.util.Objects;

public class Record {
    private int ID;
    private int value;

    public Record(int ID, int value) {
        this.ID = ID;
        this.value = value;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "Record{" +
                "ID=" + ID +
                ", value=" + value +
                '}';
    }
}
