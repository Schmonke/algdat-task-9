import java.util.PriorityQueue;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

public class ALT  {
    private int numLandmarks;
    private int numNodes;
    private int[][] fromNodeToLandmark;
    private int[][] fromLandmarkToNode;

    public ALT(int numLandmarks, int numNodes) {
        this.numLandmarks = numLandmarks;
        this.numNodes = numNodes;
        this.fromNodeToLandmark = new int[numLandmarks][numNodes];
        this.fromLandmarkToNode = new int[numLandmarks][numNodes];
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
        
        return res1 > res2 ? res1 : res2;
    }

    private int findEstimate(int from, int to, int landmarkPreprocessNumber) {
        int estimate = 0;
        int resEstimate = 0;
        for (int i = 0; i < numLandmarks; i++) {
            estimate = findLandmarkEstimate(from, to, landmarkPreprocessNumber); // use int number of wanted nodes.
            if (estimate > resEstimate) resEstimate = estimate;
        }
        return resEstimate;
    }

    public int search(Graph graph, int startNumber, int endNumber, int[] landmarkNumbers) {
        Node startNode = graph.getNodes()[startNumber];
        Node endNode = graph.getNodes()[endNumber];
        int estimate = 0;
        for (int i = 0; i < landmarkNumbers.length; i++) { //loops through landmark dimension of preprocessed
            estimate = findEstimate(from, to, i); //i represent i'th landmark in the preprocessed landmark table. 
        }

        
    }    
}
