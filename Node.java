import java.util.LinkedList;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Node {
    private int number;
    private LinkedList<Edge> edges;
    private double latitude; //Breddegrad
    private double longitude; //Langdegrad

    private Node previous;
    private boolean visited;
    private boolean enqueued;
    private int distance;
    private int estimatedDistance = -1;

    public Node(int number, double latitude, double longitude) {
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = new LinkedList<Edge>();
    }

    public int findSumDistance() {
        return distance + estimatedDistance;
    }
    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(int estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
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

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isEnqueued() {
        return enqueued;
    }

    public void setEnqueued(boolean enqueued) {
        this.enqueued = enqueued;
    }

    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(number);
        objectOutputStream.writeDouble(latitude);
        objectOutputStream.writeDouble(longitude);
        for (Edge edge : edges) {
            edge.serialize(objectOutputStream);
        }
    }

    @Override
    public String toString() {
        return "Node [" + number + "]";
    }
}
