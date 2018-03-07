package server.message;

public class Write extends Message {

    String file;
    int data;

    public Write(String sender, String type, long clock, String file, int data) {
        super(sender, "write", clock);
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
