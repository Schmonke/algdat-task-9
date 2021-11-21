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
        PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
        graph.reset();

        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];
        Node endNode = endNodeNumber == -1 
            ? new Node(0, 0)
            : nodes[endNodeNumber];
            
        startNode.setDistance(0);
        queue.add(startNodeNumber);
        
        while (!queue.isEmpty()) {
            int polledNodeNumber = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
            Node polledNode = nodes[polledNodeNumber];
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);

            polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen.
                int toNodeNumber = edge.getToNodeNumber();
                Node toNode = nodes[toNodeNumber];
                int newDistance = polledNode.getDistance() + edge.getLength();

                //look for the shortest path
                if (newDistance < toNode.getDistance()) {
                    toNode.setDistance(newDistance);
                    toNode.setPrevious(polledNode);
                    if (!toNode.isVisited()) {
                        queue.remove(toNodeNumber);
                        queue.add(toNodeNumber);
                        toNode.setEnqueued(true);
                    }
                }

                if (!toNode.isVisited() && !toNode.isEnqueued()) { //Currently doesn't get used at all
                    queue.add(toNodeNumber);
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

    //Search for the nearest landmark based on category
    public int[] searchNearest(int startNodeNumber, int numbers, PointOfInterestCategory category) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
        graph.reset();

        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];
        int[] visited = new int[numbers];
        int visitedCount = 0;
            
        startNode.setDistance(0);
        queue.add(startNodeNumber);
        
        while (!queue.isEmpty()) {
            int polledNodeNumber = queue.poll();
            Node polledNode = nodes[polledNodeNumber]; // Trekker alltid den noden som har minst avstand til kilden.
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);

            PointOfInterest pointOfInterest = graph.getPointsOfInterest().getPointOfInterest(polledNodeNumber);
            if (pointOfInterest != null && pointOfInterest.getCategory() == category) {
                visited[visitedCount++] = polledNodeNumber;
            }

            polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen. 
                int toNodeNumber = edge.getToNodeNumber();
                Node toNode = nodes[toNodeNumber];
                int newDistance = polledNode.getDistance() + edge.getLength();

                //look for the shortest path
                if (newDistance < toNode.getDistance()) {
                    
                    toNode.setDistance(newDistance);
                    toNode.setPrevious(polledNode);
                    if (!toNode.isVisited()) {
                        queue.remove(toNodeNumber);
                        queue.add(toNodeNumber);
                        toNode.setEnqueued(true);
                    }
                }

                if (!toNode.isVisited() && !toNode.isEnqueued()) { //Currently doesn't get used at all
                    queue.add(toNodeNumber);
                    toNode.setEnqueued(true);
                }
            });

            if (visited[numbers-1] != 0) { //If specified amount of numbers are not filled up
                break;
            }
        }
        return visited;
    }

    class IndexComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer int1, Integer int2) {
            return graph.getNodes()[int1].getDistance() - graph.getNodes()[int2].getDistance();
        }
    }
}