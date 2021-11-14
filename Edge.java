public class Edge {
    private Node to;
    private int drivetime;
    private int length;
    private int speedlimit;
    
    public Edge(Node to, int drivetime, int length, int speedlimit) {
        this.to = to;
        this.drivetime = drivetime;
        this.length = length;
        this.speedlimit = speedlimit;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
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

    @Override
    public String toString() {
        return "Edge [drivetime=" + drivetime + ", length=" + length + ", speedlimit=" + speedlimit + ", to=" + to
                + "]";
    }
}