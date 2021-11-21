import java.util.Comparator;
import java.util.PriorityQueue;

public class ALT  {
    private final Graph graph;
    private final PriorityQueue<Node> queue;
    private final ALTPreprocessor preprocessor;
    private final int[] landmarkNumbers;

    public ALT(Graph graph, Graph inverseGraph, int[] landmarkNumbers) {
        this.graph = graph;
        this.landmarkNumbers = landmarkNumbers;
        this.queue = new PriorityQueue<>(graph.getNodes().length, new DistanceComparator());
        this.preprocessor = new ALTPreprocessor(graph, inverseGraph, landmarkNumbers);
        
        this.preprocessor.preprocess();
    }

    private int zeroIfNegative(int result) {
        return result < 0 ? 0 : result; 
    }

    // Aims to find the estimated distance from "n" to "target"
    private int findLandmarkEstimate(int from, int to, int landmarkIndex) {
        int landmarkToTarget = preprocessor.fromLandmark(landmarkIndex, to);
        int landmarkToCurrent = preprocessor.fromLandmark(landmarkIndex, from);
        int currentToLandmark = preprocessor.toLandmark(landmarkIndex, from);
        int targetToLandmark = preprocessor.toLandmark(landmarkIndex, to);

        int res1 = zeroIfNegative(landmarkToTarget - landmarkToCurrent);
        int res2 = zeroIfNegative(currentToLandmark - targetToLandmark);
        // System.out.println("res1: " + landmarkToTarget + " - " + landmarkToCurrent + " = " + res1 
        //     + " \tres2: " + currentToLandmark + " - " + targetToLandmark + " = " + res2);
        
        return res1 > res2 ? res1 : res2;
    }

    private int findEstimate(int from, int to) {
        int estimate = -1;
        int tempEstimate = -1;
        for (int i = 0; i < landmarkNumbers.length; i++) { //loops through landmark dimension of preprocessed
            tempEstimate = findLandmarkEstimate(from, to, i); //i represent i'th landmark in the preprocessed landmark table.
            if (tempEstimate > estimate) estimate = tempEstimate;
        }
        return estimate;
    }

    

    public int search(int startNumber, int endNumber) {        
        queue.clear();
        graph.reset();

        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNumber];
        Node endNode = nodes[endNumber];

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
                        if (toNode.getEstimatedDistance() == -1) {
                            int estimate = findEstimate(polledNode.getNumber(), toNode.getNumber());
                            //System.out.println(estimate);
                            toNode.setEstimatedDistance(estimate);
                        }
                        queue.add(toNode);
                        toNode.setEnqueued(true);
                    }
                }
                //System.out.println(!toNode.isEnqueued());
                if (!toNode.isVisited() && !toNode.isEnqueued()) {
                    System.out.println("GEH - ALT");
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
            return o1.findSumDistance() - o2.findSumDistance();
        }
    }
}
