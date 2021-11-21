import java.io.IOException;
import java.io.ObjectOutputStream;

public class Edge {
    private int toNodeNumber;
    private int drivetime;
    private int length;
    private int speedlimit;
    
    public Edge(int toNodeNumber, int drivetime, int length, int speedlimit) {
        this.toNodeNumber = toNodeNumber;
        this.drivetime = drivetime;
        this.length = length;
        this.speedlimit = speedlimit;
    }

    public int getToNodeNumber() {
        return toNodeNumber;
    }

    public void setToNodeNumber(int toNodeNumber) {
        this.toNodeNumber = toNodeNumber;
    }

    public int getDrivetime() {
        return drivetime;
    }

    public void setDrivetime(int drivetime) {
        this.drivetime = drivetime;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSpeedlimit() {
        return speedlimit;
    }

    public void setSpeedlimit(int speedlimit) {
        this.speedlimit = speedlimit;
    }

    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(toNodeNumber);
        objectOutputStream.writeInt(drivetime);
        objectOutputStream.writeInt(length);
        objectOutputStream.writeInt(speedlimit);
    }

    @Override
    public String toString() {
        return "Edge [drivetime=" + drivetime + ", length=" + length + ", speedlimit=" + speedlimit + ", to=" + toNodeNumber
                + "]";
    }
}