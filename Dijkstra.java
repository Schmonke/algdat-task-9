import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    private final Graph graph;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    // Korteste vei representeres som en lenket liste som går bakover fra mål til start.
    // Returnerer distanse fra start til sluttnode, akkumulert gjennom søket. 
    public int search(int startNodeNumber, int endNodeNumber) {
        PriorityQueue<Node> queue = new PriorityQueue<>(graph.getNodes().length, new DistanceComparator());
        graph.reset();

        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];
        Node endNode = endNodeNumber == -1 
            ? new Node(-1, 0, 0)
            : nodes[endNodeNumber];
            
        startNode.setDistance(0);
        queue.add(startNode);
        
        while (!queue.isEmpty()) {
            Node polledNode = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);

            polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen. 
                Node toNode = nodes[edge.getToNodeNumber()];
                int newDistance = polledNode.getDistance() + edge.getLength();
                //System.out.println(newDistance);

                //look for the shortest path
                if (newDistance < toNode.getDistance()) {
                    toNode.setDistance(newDistance);
                    toNode.setPrevious(polledNode);
                    if (!toNode.isVisited()) {
                        queue.remove(toNode);
                        queue.add(toNode);
                        toNode.setEnqueued(true);
                    }
                }

                if (!toNode.isVisited() && !toNode.isEnqueued()) { //Currently doesn't get used at all
                    queue.add(toNode);
                    toNode.setEnqueued(true);
                }
            });

            if (endNode.isVisited()) {
                break;
            }
        }
        return endNode.getDistance();
    }

    class DistanceComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.getDistance() - o2.getDistance();
        }
    }
}