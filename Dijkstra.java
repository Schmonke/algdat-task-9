import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Supplier;

public class Dijkstra {
    private final Graph graph;
    private final PriorityQueue<Node> queue;

    public Dijkstra(Graph graph) {
        this.graph = graph;
        queue = new PriorityQueue<>(graph.getNodes().length, new DistanceComparator());
    }

    // Korteste vei representeres som en lenket liste som går bakover fra mål til start.
    // Returnerer distanse fra start til sluttnode, akkumulert gjennom søket. 
    public void search(int startNodeNumber, int endNodeNumber) {
        queue.clear();
        for (Node node : graph.getNodes()) {
            node.setDistance(Integer.MAX_VALUE);
            node.setEnqueued(false);
            node.setPrevious(null);
            node.setVisited(false);
        }

        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];
        Node endNode = endNodeNumber == -1 
            ? new Node(-1, 0, 0)
            : nodes[endNodeNumber];
            
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node polledNode = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);

            polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen. 
                Node toNode = nodes[edge.getToNodeNumber()];
                int newDistance = polledNode.getDistance() + edge.getLength();

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

                if (!toNode.isVisited() && !toNode.isEnqueued()) {
                    queue.add(toNode);
                    toNode.setEnqueued(true);
                }
            });

            if (endNode.isVisited()) {
                break;
            }
        }
    }

    class DistanceComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.getDistance() - o2.getDistance();
        }
    }
}