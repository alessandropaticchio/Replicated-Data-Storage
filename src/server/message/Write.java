package server.message;

public class Write extends Message {

    String file;
    int data;

    public Write(String sender, String type, String file, int data) {
        super(sender, "write");
        this.file = file;
        this.data = data;
    }

    public String getFile() {
        return file;
    }

    public int getData() {
        return data;
    }

}
