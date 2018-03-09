package client;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    String dataID;

    public ClientMessage(String dataID) {
        this.dataID = dataID;
    }

    public String getDataID() {
        return dataID;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }
}
