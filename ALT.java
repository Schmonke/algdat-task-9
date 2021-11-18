import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

public class ALT  {
    private int numLandmarks;
    private int numNodes;
    private PriorityQueue<Node> queue;
    private int[][] fromNodeToLandmark;
    private int[][] fromLandmarkToNode;

    public ALT(int numLandmarks, int numNodes, String preprocessedFilePath) {
        this.numLandmarks = numLandmarks;
        this.numNodes = numNodes;
        queue = new PriorityQueue<>(numNodes, new DistanceComparator());
        this.fromNodeToLandmark = new int[numLandmarks][numNodes];
        this.fromLandmarkToNode = new int[numLandmarks][numNodes];
        readPreprocessedFile(preprocessedFilePath);
    }

    private void readPreprocessedFile(String path) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(path));
            ALTPreprocessor altPreprocessor = (ALTPreprocessor)objectInputStream.readObject();
            this.fromNodeToLandmark = altPreprocessor.getToLandmark();
            this.fromLandmarkToNode = altPreprocessor.getFromLandmark();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private int zeroIfNegative(int result) {
        return result < 0 ? 0 : result; 
    }

    private int findLandmarkEstimate(int from, int to, int landmarkPreprocessNumber) {
        int landmarkToTarget = fromLandmarkToNode[landmarkPreprocessNumber][to];
        int landmarkToCurrent = fromLandmarkToNode[landmarkPreprocessNumber][from];
        int targetToLandmark = fromNodeToLandmark[landmarkPreprocessNumber][to];
        int currentToLandmark = fromNodeToLandmark[landmarkPreprocessNumber][from];

        int res1 = zeroIfNegative(landmarkToTarget - landmarkToCurrent);
        int res2 = zeroIfNegative(currentToLandmark - targetToLandmark);
        // System.out.println("res1: " + landmarkToTarget + " - " + landmarkToCurrent + " = " + res1 
        //     + " \tres2: " + currentToLandmark + " - " + targetToLandmark + " = " + res2);
        
        return res1 > res2 ? res1 : res2;
    }

    private int findEstimate(int from, int to) {
        int estimate = -1;
        int tempEstimate = -1;
        for (int i = 0; i < numLandmarks; i++) { //loops through landmark dimension of preprocessed
            tempEstimate = findLandmarkEstimate(from, to, i); //i represent i'th landmark in the preprocessed landmark table.
            if (tempEstimate > estimate) estimate = tempEstimate;
        }
        return estimate;
    }

    public int search(Graph graph, int startNumber, int endNumber, int[] landmarkNumbers) {        
        queue.clear();
        for (Node node : graph.getNodes()) {
            node.setDistance(Integer.MAX_VALUE);
            node.setEnqueued(false);
            node.setPrevious(null);
            node.setVisited(false);
        }

        int result = -1;
        Node[] nodes = graph.getNodes();
        Node startNode = nodes[startNumber];
        Node endNode = nodes[endNumber];
            
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node polledNode = queue.poll(); // Trekker alltid den noden som har minst avstand til kilden.
            polledNode.setEnqueued(false);
            polledNode.setVisited(true);
            if(polledNode.getDistance() == Integer.MAX_VALUE) polledNode.setDistance(0);
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
                        int estimate = findEstimate(polledNode.getNumber(), toNode.getNumber());
                        //System.out.println(estimate);
                        toNode.setEstimatedDistance(estimate);
                        queue.add(toNode);
                        toNode.setEnqueued(true);
                    }
                }
                //System.out.println(!toNode.isEnqueued());
                if (!toNode.isVisited() && !toNode.isEnqueued()) {
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

    public int[][] getFromNodeToLandmark() {
        return fromNodeToLandmark;
    }

    public int[][] getFromLandmarkToNode() {
        return fromLandmarkToNode;
    }

    class DistanceComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.findSumDistance() - o2.findSumDistance();
        }
    }
}
