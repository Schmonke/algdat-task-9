import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Supplier;

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
        this.startnode = nodes[startnodeNumber];
        this.endnode = endnodeNumber == -1 ? null : nodes[endnodeNumber];
        this.queue = new PriorityQueue<>(nodes.length, new DistanceComparator());
        startnode.setDistance(0);
        //Arrays.stream(graph.getNodes()).forEach(node -> this.queue.add(node));
    }

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
        queue = new PriorityQueue<>(nodes.length, new DistanceComparator());
        startnode.getEdges().forEach(edge -> {
            Node toNode = edge.getTo();
            toNode.setDistance(edge.getLength());
            queue.add(toNode);
        });

        Supplier<Boolean> condition = endnode == null
            ? () -> !queue.isEmpty()
            : () -> !endnode.isVisited();

        while (condition.get()) {
            Node polledNode = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.

            polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen. 
                Node toNode = edge.getTo();
                int newDistance = polledNode.getDistance() + edge.getLength();
                //look for the shortest path
                if(toNode.getDistance()==Integer.MAX_VALUE || toNode.getDistance()>newDistance){
                    toNode.setDistance(newDistance);
                    toNode.setPrevious(polledNode);
                }
                if (!toNode.isVisited()) {
                    queue.add(toNode);
                }
            });
            polledNode.setVisited(true);
        }
        System.out.println(endnode.getNumber());
        return endnode.getDistance();
    }
}