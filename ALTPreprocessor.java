import java.util.Objects;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ALTPreprocessor implements Serializable {
    transient private final Graph graph;
    transient private final Graph inverseGraph;
    transient private final int[] landmarkNodeNumbers;

    private final int[][] fromLandmark;
    private final int[][] toLandmark;


    public ALTPreprocessor(Graph graph, Graph inverseGraph, int[] landmarkNodeNumbers) {
        this.graph = Objects.requireNonNull(graph, "graph is null");
        this.inverseGraph = Objects.requireNonNull(inverseGraph, "inversegraph is null");
        this.landmarkNodeNumbers = Objects.requireNonNull(landmarkNodeNumbers, "landmarNodeNumbers are null").clone();
        
        this.fromLandmark = new int[landmarkNodeNumbers.length][graph.getNodes().length];
        this.toLandmark = new int[landmarkNodeNumbers.length][graph.getNodes().length];
    }
    
    private void fillArrayWithDistanceData(Graph graph, int[][] array) {
        Dijkstra dijkstra = new Dijkstra(graph);
        for (int i = 0; i < landmarkNodeNumbers.length; i++) {
            dijkstra.search(landmarkNodeNumbers[i], -1);
            Node[] nodes = graph.getNodes();
            //System.out.println(nodes[i].getDistance());
            for (int j = 0; j < nodes.length; j++) {
                array[i][j] = nodes[j].getDistance();
                //System.out.println(array[i][j]);
            }
        }
    }
    
    public void preprocess(String path) {
        fillArrayWithDistanceData(graph, fromLandmark);
        fillArrayWithDistanceData(inverseGraph, toLandmark);

        dumpToFile(path);
    }

    // private boolean readCached() {
        
    // }
    
    private void dumpToFile(String path) {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(path));
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int[][] getFromLandmark() {
        return fromLandmark;
    }

    public int[][] getToLandmark() {
        return toLandmark;
    }
}