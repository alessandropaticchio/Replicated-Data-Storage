package client;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    int dataID;

    public ClientMessage(int dataID) {
        this.dataID = dataID;
    }

    public int getDataID() {
        return dataID;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }
}
