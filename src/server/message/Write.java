package server.message;

import java.net.Socket;

public class Write extends Message {

    int file;
    int data;
    int sn; //sequence number
    String socketString;

    public Write(int sender, int file, int data, int sn, String socketString) {
        super(sender);
        this.file = file;
        this.data = data;
        this.sn = sn;
        this.socketString = socketString;
    }

    public int getFile() {
        return file;
    }

    public int getData() {
        return data;
    }

    public String getSocketString() {
        return socketString;
    }

    public int getSn() { return sn; }

    public String toString() {
        return "WRITE - CLK: " + getClock() + "ID: " + file + ", DT: " + data + " , SCK: " + socketString;
    }

}
