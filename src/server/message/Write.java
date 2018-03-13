package server.message;

import java.net.Socket;

public class Write extends Message {

    int file;
    int data;
    Socket socket;

    public Write(int sender, int file, int data, Socket socket) {
        super(sender);
        this.file = file;
        this.data = data;
        this.socket = socket;
    }

    public int getFile() {
        return file;
    }

    public int getData() {
        return data;
    }

    public Socket getSocket() {
        return socket;
    }

}
