package client;

public class WriteMessage extends ClientMessage {

    int value;


    public WriteMessage(String dataID, int value) {
        super(dataID);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
