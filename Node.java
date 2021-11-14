import java.util.LinkedList;

public class Node {
    private int number;
    private Node previous;
    private boolean visited;
    private int distance;
    private double latitude; //Breddegrad
    private double longitude; //Langdegrad
    private LinkedList<Edge> edges;

    public Node(int number, Node previous, boolean visited, int distance, double latitude, double longitude,
            LinkedList<Edge> edges) {
        this.number = number;
        this.previous = previous;
        this.visited = visited;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = edges;
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
