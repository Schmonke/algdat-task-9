import java.io.Serializable;
import java.util.LinkedList;

public class Node implements Serializable {
    private int number;
    private LinkedList<Edge> edges;
    private int distance;
    private double latitude; //Breddegrad
    private double longitude; //Langdegrad

    private transient Node previous;
    private transient boolean visited;
    private transient boolean enqueued;

    public Node(int number, double latitude, double longitude) {
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = new LinkedList<Edge>();
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public Node getPrevious() {
        return previous;
    }
    public void setPrevious(Node previous) {
        this.previous = previous;
    }
    public boolean isEnqueued() {
        return enqueued;
    }
    public void setEnqueued(boolean enqueued) {
        this.enqueued = enqueued;
    }
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public LinkedList<Edge> getEdges() {
        return edges;
    }
    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }
    @Override
    public String toString() {
        return "Node [" + number + "]";
    }
}
