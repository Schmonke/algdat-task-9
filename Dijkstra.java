import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    private final Graph graph;
    private int pollCount = 0;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    public int getPrevPollCount() {
        return pollCount;
    }

    private int[] search(Timer timer, int startNodeNumber, NodeCondition condition, int maxNodes) {
        boolean haveTimer = false;
        if (timer != null) haveTimer = true;
        if (haveTimer) timer.start();
        PriorityQueue<Integer> queue = new PriorityQueue<>(graph.getNodes().length, new IndexComparator());
        graph.reset();
        pollCount = 0;
        
        int foundNodeIndex = 0;
        int[] foundNodes = new int[maxNodes];
        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNodeNumber];

        startNode.setDistance(0);
        startNode.setDriveTime(0);
        queue.add(startNodeNumber);
        
        while (!queue.isEmpty()) {
            int polledNodeNumber = queue.poll();
            pollCount++;
            Node polledNode = nodes[polledNodeNumber];
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);

            if (condition.isCorrect(polledNodeNumber)) {
                foundNodes[foundNodeIndex++] = polledNodeNumber;
                if (foundNodeIndex == maxNodes) {
                    break;
                }
            }

            polledNode.getEdges().forEach(edge -> {
                int toNodeNumber = edge.getToNodeNumber();
                Node toNode = nodes[toNodeNumber];
                int newDistance = polledNode.getDistance() + edge.getLength();

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
            });
        }
        if (haveTimer) timer.end();
        return Arrays.copyOf(foundNodes, foundNodeIndex);
    }

    public int searchToNode(Timer timer, int startNodeNumber, int endNodeNumber) {
        int[] foundNodes = search(timer, startNodeNumber, (index) -> index == endNodeNumber, 1);
        return foundNodes.length != 0 ? foundNodes[0] : -1;
    }

    public int[] searchNearest(Timer timer, int startNodeNumber, PointOfInterestCategory category, int maxNodes) {
        NodeCondition condition = (index) -> {
            PointOfInterest pointOfInterest = graph.getPointsOfInterest().getPointOfInterest(index);
            return pointOfInterest != null && pointOfInterest.getCategory() == category;
        };
        
        return search(timer, startNodeNumber, condition, maxNodes);
    }

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