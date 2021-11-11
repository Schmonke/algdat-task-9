import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Dijkstra {
    private Node[] nodes;
    private int startnodeNumber;
    private int endnodeNumber;
    private Node startnode;
    private Node endnode;
    private PriorityQueue<Node> queue; // Dijkstra bruker avstand fra startnode som prioritet

    public Dijkstra(Node[] nodes, int startnodeNumber, int endnodeNumber) {
        this.nodes = nodes;
        this.startnodeNumber = startnodeNumber;
        this.startnodeNumber = startnodeNumber;
        this.startnode = nodes[startnodeNumber];
        this.endnode = nodes[endnodeNumber];
        this.queue = new PriorityQueue<>(nodes.length, new DistanceComparator());
        //Arrays.stream(graph.getNodes()).forEach(node -> this.queue.add(node));
    }

    // class DijkstraNode extends Node {
    //     private int lengthToStartNode;

    //     public DijkstraNode(int number, double latitude, double longitude, LinkedList<Edge> edges) {
    //         super(number, latitude, longitude, edges);
    //         this.lengthToStartNode = 0;
    //     }

    //     public int getLengthToStartNode() {
    //         return lengthToStartNode;
    //     }

    //     public void setLengthToStartNode(int lengthToStartNode) {
    //         this.lengthToStartNode = lengthToStartNode;
    //     }
        
        
    // }

    class DistanceComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.getDistance() - o2.getDistance();
        }
    }

    class Estimate {

    }

    public void shorten() {

    }

    // Korteste vei representeres som en lenket liste som går bakover fra mål til start.
    // Returnerer distanse fra start til sluttnode, akkumulert gjennom søket. 
    public int search() {
        startnode.getEdges().forEach(edge -> {
            Node toNode = edge.getTo();
            toNode.setDistance(edge.getLength());
            queue.add(toNode);
        });

        int i = 0;
        while (!endnode.isVisited()) {
            //if (i == startnodeNumber) continue;
            Node polledNode = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
            polledNode.setVisited(true);
            polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen. 
                Node toNode = edge.getTo();
                int newDistance = polledNode.getDistance() + edge.getLength();
                if (newDistance < toNode.getDistance()) {
                    toNode.setDistance(newDistance);
                    toNode.setPrevious(polledNode);
                }
                if (!toNode.isVisited()) {
                    queue.add(toNode);
                }
            });
            polledNode.setVisited(true);
            i++;
        }
        return endnode.getDistance();
    }
}