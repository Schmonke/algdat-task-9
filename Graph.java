import java.io.Serializable;

public class Graph implements Serializable {
    private Node[] nodes;
    private PointsOfInterest pointsOfInterest;

    public Graph() {
        
    }

    public Graph(Node[] nodes) {
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public PointsOfInterest getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(PointsOfInterest pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public void reset() {
        for (Node node : nodes) {
            node.setDistance(Integer.MAX_VALUE);
            node.setEnqueued(false);
            node.setPrevious(null);
            node.setVisited(false);
        }
    }
}
