package server.message;

import java.net.Socket;

public class Write extends Message {

    int file;
    int data;
    String socketString;

    public Write(int sender, int file, int data, String socketString) {
        super(sender);
        this.file = file;
        this.data = data;
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

    public String toString() {
        return "WRITE - CLK: " + getClock() + "ID: " + file + ", DT: " + data + " , SCK: " + socketString;
    }

}
