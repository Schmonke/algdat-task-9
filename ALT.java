import java.util.Comparator;
import java.util.PriorityQueue;

public class ALT  {
    private final Graph graph;
    private final ALTPreprocessor preprocessor;
    private final int[] landmarkNumbers;
    private int pollCount = 0;

    public ALT(Graph graph, Graph inverseGraph, int[] landmarkNumbers) {
        this.graph = graph;
        this.landmarkNumbers = landmarkNumbers;
        this.preprocessor = new ALTPreprocessor(graph, inverseGraph, landmarkNumbers);
        
        this.preprocessor.preprocess();
    }

    private int zeroIfNegative(int result) {
        return result < 0 ? 0 : result; 
    }

    private int findLandmarkEstimate(int from, int to, int landmarkIndex) {
        int landmarkToTarget = preprocessor.fromLandmark(landmarkIndex, to);
        int landmarkToCurrent = preprocessor.fromLandmark(landmarkIndex, from);
        int currentToLandmark = preprocessor.toLandmark(landmarkIndex, from);
        int targetToLandmark = preprocessor.toLandmark(landmarkIndex, to);

        int res1 = zeroIfNegative(landmarkToTarget - landmarkToCurrent);
        int res2 = zeroIfNegative(currentToLandmark - targetToLandmark);
        
        return res1 > res2 ? res1 : res2;
    }

    private int findEstimate(int from, int to) {
        int estimate = -1;
        int tempEstimate = -1;
        for (int i = 0; i < landmarkNumbers.length; i++) {
            tempEstimate = findLandmarkEstimate(from, to, i);
            if (tempEstimate > estimate) estimate = tempEstimate;
        }
        return estimate;
    }

    public int getPrevPollCount() {
        return pollCount;
    }

    public int search(Timer timer, int startNodeNumber, int endNodeNumber) {      
        timer.start();
        PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
        graph.reset();
        pollCount = 0;

        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];
        Node endNode = nodes[endNodeNumber];

        startNode.setDriveTime(0);
        startNode.setDistance(0);    
        queue.add(startNodeNumber);

        while (!queue.isEmpty()) {
            int polledNodeNumber = queue.poll();
            pollCount++;
            Node polledNode = nodes[polledNodeNumber];
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);
            
            polledNode.getEdges().forEach(edge -> {
                int toNodeNumber = edge.getToNodeNumber();
                Node toNode = nodes[edge.getToNodeNumber()];
                int newDistance = polledNode.getDistance() + edge.getLength();

                if (newDistance < toNode.getDistance()) {
                    toNode.setDistance(newDistance);
                    toNode.setPrevious(polledNodeNumber);
                    toNode.setDriveTime(polledNode.getDriveTime() + edge.getDrivetime());                    
                    if (!toNode.isVisited()) {
                        queue.remove(toNodeNumber);
                        if (toNode.getEstimatedDistance() == -1) {
                            int estimate = findEstimate(toNodeNumber, endNodeNumber);
                            toNode.setEstimatedDistance(estimate);
                        }
                        queue.add(toNodeNumber);
                        toNode.setEnqueued(true);
                    }
                }
            });

            if (endNode.isVisited()) {
                break;
            }
        }
        timer.end();
        return endNode.getDistance();
    }

    class IndexComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer int1, Integer int2) {
            return graph.getNodes()[int1].findSumDistance() - graph.getNodes()[int2].findSumDistance();
        }
    }
}
