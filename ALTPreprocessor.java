import java.util.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ALTPreprocessor {
    private final Graph graph;
    private final Graph inverseGraph;
    private final int[] landmarkNodeNumbers;

    private int[][] fromLandmark;
    private int[][] toLandmark;

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
            System.out.println("Landmark: " + i);
            dijkstra.search(landmarkNodeNumbers[i], -1);
            Node[] nodes = graph.getNodes();
            //System.out.println(nodes[i].getDistance());
            for (int j = 0; j < nodes.length; j++) {
                array[i][j] = nodes[j].getDistance();
                //System.out.println(array[i][j]);
            }
        }
    }
    
    public void preprocess() {
        String fileName = preprocessedFileName();
        boolean cached;
        try {
            cached = readCached(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (cached) {
            System.out.println("We cached the lil' homie!");
        }

        if (!cached) {
            fillArrayWithDistanceData(graph, fromLandmark);
            fillArrayWithDistanceData(inverseGraph, toLandmark);

            dumpToFile(fileName);
        }
    }

    private void readFromFile(String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ) {
            fromLandmark = (int[][])objectInputStream.readObject();
            toLandmark = (int[][])objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpToFile(String path) {
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        ) {
            objectOutputStream.writeObject(fromLandmark);
            objectOutputStream.writeObject(toLandmark);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String preprocessedFileName() {
        graph.reset();
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");  
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash;
        try (
            DigestOutputStream digestOutputStream = new DigestOutputStream(OutputStream.nullOutputStream(), digest);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(digestOutputStream);
        ) {
            objectOutputStream.writeObject(landmarkNodeNumbers);
            objectOutputStream.writeObject(graph);

            hash = digest.digest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder targetFileNameBuilder = new StringBuilder();
        targetFileNameBuilder.append("preprocessed_");
        for (byte b : hash) {
            targetFileNameBuilder.append(String.format("%02X", b));
        }
        return targetFileNameBuilder.toString();
    }

    private boolean readCached(String fileName) throws NoSuchAlgorithmException, IOException {
        File targetFile = new File(fileName);
        if (targetFile.exists()) {
            readFromFile(targetFile.getAbsolutePath());
            return true;
        } else {
            return false;
        }
    }

    public int fromLandmark(int landmarkIndex, int targetNode) {
       return this.fromLandmark[landmarkIndex][targetNode]; 
    }

    public int toLandmark(int landmarkIndex, int sourceNode) {
        return this.toLandmark[landmarkIndex][sourceNode];
    }

    // For comparing
    public int[][] getFromLandmark() {
        return fromLandmark;
    }

    public int[][] getToLandmark() {
        return toLandmark;
    }
}