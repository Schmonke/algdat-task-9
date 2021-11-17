import java.io.Serializable;

public class Graph implements Serializable {
    private Node[] nodes;

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
}
