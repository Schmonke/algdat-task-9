import java.util.Objects;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ALTPreprocessor  {
    private final Graph graph;
    private final Graph inverseGraph;
    private final int[] landmarkNodeNumbers;

    private final int[][] fromLandmark;
    private final int[][] toLandmark;


    public ALTPreprocessor(Graph graph, Graph inverseGraph, int[] landmarkNodeNumbers) {
        this.graph = Objects.requireNonNull(graph, "graph is null");
        this.inverseGraph = Objects.requireNonNull(inverseGraph, "inversegraph is null");
        this.landmarkNodeNumbers = Objects.requireNonNull(landmarkNodeNumbers, "landmarNodeNumbers are null").clone();
        
        this.fromLandmark = new int[landmarkNodeNumbers.length][graph.getNodes().length];
        this.toLandmark = new int[landmarkNodeNumbers.length][graph.getNodes().length];
    }
    
    private void fillArrayWithDistanceData(Dijkstra dijkstra, Graph graph, int[][] array) {
        for (int i = 0; i < landmarkNodeNumbers.length; i++) {
            dijkstra.search(landmarkNodeNumbers[i], -1);
            Node[] nodes = graph.getNodes();
            for (int j = 0; j < nodes.length; j++) {
                array[i][j] = nodes[j].getDistance();
            }
        }
    }
    
    public void preprocess() {
        Dijkstra dijkstraForward = new Dijkstra(graph);
        Dijkstra dijkstraReversed = new Dijkstra(inverseGraph);

        fillArrayWithDistanceData(dijkstraForward, graph, fromLandmark);
        fillArrayWithDistanceData(dijkstraReversed, inverseGraph, toLandmark);

        
    }

    // private boolean readCached() {
        
    // }
    
    private void dumpToFile(String path) throws IOException {
        try{
        java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(new FileWriter(path));
        for(int i = 0; i < fromLandmark.length; i++){ //5 times
            for(int j = 0; j < toLandmark.length; i++){ //5 times
                bufferedWriter.write(fromLandmark[i][j]);
            }
        }
        }catch(IOException e){
            e.getStackTrace();
        }
    }
}