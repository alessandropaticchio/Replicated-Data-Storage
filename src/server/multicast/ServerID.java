package server.multicast;

public class ServerID {

    private String ip;
    private int port;

    public ServerID(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

}
