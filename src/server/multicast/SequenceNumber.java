package server.multicast;

public class SequenceNumber {

    private int sn;
    private int mod;
    private int window;

    public SequenceNumber() {
        this.sn = 0;
        this.mod = 1024;
        this.window = 256;
    }

    public int getSequenceNumber() { return sn; }

    public void setSequenceNumber(int sn) { this.sn = sn; }

    public void incrementSequenceNumber() { this.sn = (sn+1)%mod; }

    public boolean validSequenceNumber(int sn) {

        if(sn > this.sn && (sn < this.sn + window || sn < (this.sn + window)%mod))
            return true;
        else
            return false;
    }


}
