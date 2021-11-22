import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    private final Graph graph;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    private int[] search(int startNodeNumber, NodeCondition condition, int maxNodes) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
        graph.reset();
        
        int foundNodeIndex = 0;
        int[] foundNodes = new int[maxNodes];
        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];

        startNode.setDistance(0);
        startNode.setDriveTime(0);
        queue.add(startNodeNumber);
        
        while (!queue.isEmpty()) {
            int polledNodeNumber = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
            Node polledNode = nodes[polledNodeNumber];
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);

            if (condition.isCorrect(polledNodeNumber)) {
                foundNodes[foundNodeIndex++] = polledNodeNumber;
                if (foundNodeIndex == maxNodes) {
                    break;
                }
            }

            polledNode.getEdges().forEach(edge -> { // Adds all neighbours for the chosen node, until queue is empty.
                int toNodeNumber = edge.getToNodeNumber();
                Node toNode = nodes[toNodeNumber];
                int newDistance = polledNode.getDistance() + edge.getLength();

                //look for the shortest path
                if (newDistance < toNode.getDistance()) {
                    toNode.setDistance(newDistance);
                    toNode.setDriveTime(polledNode.getDriveTime() + edge.getDrivetime());
                    toNode.setPrevious(polledNodeNumber);
                    if (!toNode.isVisited()) {
                        queue.remove(toNodeNumber);
                        queue.add(toNodeNumber);
                        toNode.setEnqueued(true);
                    }
                }

                if (!toNode.isVisited() && !toNode.isEnqueued()) {
                    queue.add(toNodeNumber);
                    toNode.setEnqueued(true);
                }
            });
        }

        return Arrays.copyOf(foundNodes, foundNodeIndex);
    }

    public int searchToNode(int startNodeNumber, int endNodeNumber) {
        int[] foundNodes = search(startNodeNumber, (index) -> index == endNodeNumber, 1);
        return foundNodes.length != 0 ? foundNodes[0] : -1;
    }

    public int[] searchNearest(int startNodeNumber, PointOfInterestCategory category, int maxNodes) {
        NodeCondition condition = (index) -> {
            PointOfInterest pointOfInterest = graph.getPointsOfInterest().getPointOfInterest(index);
            return pointOfInterest != null && pointOfInterest.getCategory() == category;
        };
        
        return search(startNodeNumber, condition, maxNodes);
    }

    // Korteste vei representeres som en lenket liste som går bakover fra mål til start.
    // Returnerer distanse fra start til sluttnode, akkumulert gjennom søket. 
    // public int search(int startNodeNumber, int endNodeNumber) {
    //     PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
    //     graph.reset();

    //     Node[] nodes = graph.getNodes();
    //     Node startNode = nodes[startNodeNumber];
    //     Node endNode = endNodeNumber == -1 
    //         ? new Node(0, 0)
    //         : nodes[endNodeNumber];
            
    //     startNode.setDistance(0);
    //     queue.add(startNodeNumber);
        
    //     while (!queue.isEmpty()) {
    //         int polledNodeNumber = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
    //         Node polledNode = nodes[polledNodeNumber];
    //         polledNode.setEnqueued(false);
    //         polledNode.setVisited(true);

    //         polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen.
    //             int toNodeNumber = edge.getToNodeNumber();
    //             Node toNode = nodes[toNodeNumber];
    //             int newDistance = polledNode.getDistance() + edge.getLength();

    //             //look for the shortest path
    //             if (newDistance < toNode.getDistance()) {
    //                 toNode.setDistance(newDistance);
    //                 toNode.setPrevious(polledNode);
    //                 if (!toNode.isVisited()) {
    //                     queue.remove(toNodeNumber);
    //                     queue.add(toNodeNumber);
    //                     toNode.setEnqueued(true);
    //                 }
    //             }

    //             if (!toNode.isVisited() && !toNode.isEnqueued()) { //Currently doesn't get used at all
    //                 queue.add(toNodeNumber);
    //                 toNode.setEnqueued(true);
    //             }
    //         });

    //         if (endNode.isVisited()) {
    //             break;
    //         }
    //     }
    //     return endNode.getDistance();
    // }

    
    // class DistanceComparator implements Comparator<Node> {
    //     @Override
    //     public int compare(Node o1, Node o2) {
    //         return o1.getDistance() - o2.getDistance();
    //     }
    // }

    // //Search for the nearest landmark based on category
    // public int[] searchNearest(int startNodeNumber, int numbers, PointOfInterestCategory category) {
    //     PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
    //     graph.reset();

    //     Node[] nodes = graph.getNodes();
    //     Node startNode = nodes[startNodeNumber];
    //     int[] visited = new int[numbers];
    //     int visitedCount = 0;
            
    //     startNode.setDistance(0);
    //     queue.add(startNodeNumber);
        
    //     while (!queue.isEmpty()) {
    //         int polledNodeNumber = queue.poll();
    //         Node polledNode = nodes[polledNodeNumber]; // Trekker alltid den noden som har minst avstand til kilden.
    //         polledNode.setEnqueued(false);
    //         polledNode.setVisited(true);

    //         PointOfInterest pointOfInterest = graph.getPointsOfInterest().getPointOfInterest(polledNodeNumber);
    //         if (pointOfInterest != null && pointOfInterest.getCategory() == category) {
    //             visited[visitedCount++] = polledNodeNumber;
    //         }

    //         polledNode.getEdges().forEach(edge -> { // Legger til alle nabonoder for den valgte noden, til køen. 
    //             int toNodeNumber = edge.getToNodeNumber();
    //             Node toNode = nodes[toNodeNumber];
    //             int newDistance = polledNode.getDistance() + edge.getLength();

    //             //look for the shortest path
    //             if (newDistance < toNode.getDistance()) {
                    
    //                 toNode.setDistance(newDistance);
    //                 toNode.setPrevious(polledNode);
    //                 if (!toNode.isVisited()) {
    //                     queue.remove(toNodeNumber);
    //                     queue.add(toNodeNumber);
    //                     toNode.setEnqueued(true);
    //                 }
    //             }

    //             if (!toNode.isVisited() && !toNode.isEnqueued()) { //Currently doesn't get used at all
    //                 queue.add(toNodeNumber);
    //                 toNode.setEnqueued(true);
    //             }
    //         });

    //         if (visited[numbers-1] != 0) { //If specified amount of numbers are not filled up
    //             break;
    //         }
    //     }
    //     return visited;
    // }

    @FunctionalInterface
    interface NodeCondition {
        boolean isCorrect(int index);
    }

    class IndexComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer int1, Integer int2) {
            return graph.getNodes()[int1].getDistance() - graph.getNodes()[int2].getDistance();
        }
    }
}