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
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class ALT  {
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
                int newDriveTime = polledNode.getDriveTime() + edge.getDrivetime();

                if (newDriveTime < toNode.getDriveTime()) {
                    toNode.setDistance(polledNode.getDriveTime() + edge.getDrivetime());
                    toNode.setPrevious(polledNodeNumber);
                    toNode.setDriveTime(newDriveTime);                    
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
        return endNode.getDriveTime();
    }

    class IndexComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer int1, Integer int2) {
            return graph.getNodes()[int1].findSumDistance() - graph.getNodes()[int2].findSumDistance();
        }
    }
}


class ALTPreprocessor {
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
            dijkstra.searchToNode(null, landmarkNodeNumbers[i], -1);
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
            System.out.println("Found cached ALT preprocessing - using cached!");
        } else {
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
            for (Node node : graph.getNodes()) {
                node.serialize(objectOutputStream);
            }

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

class Dijkstra {
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
                int newDriveTime = polledNode.getDriveTime() + edge.getDrivetime();

                if (newDriveTime < toNode.getDriveTime()) {
                    toNode.setDriveTime(newDriveTime);
                    toNode.setDistance(polledNode.getDistance() + edge.getLength());
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
            return graph.getNodes()[int1].getDriveTime() - graph.getNodes()[int2].getDriveTime();
        }
    }
}

class Edge {
    private int toNodeNumber;
    private int drivetime;
    private int length;
    private int speedlimit;
    
    public Edge(int toNodeNumber, int drivetime, int length, int speedlimit) {
        this.toNodeNumber = toNodeNumber;
        this.drivetime = drivetime;
        this.length = length;
        this.speedlimit = speedlimit;
    }

    public int getToNodeNumber() {
        return toNodeNumber;
    }

    public void setToNodeNumber(int toNodeNumber) {
        this.toNodeNumber = toNodeNumber;
    }

    public int getDrivetime() {
        return drivetime;
    }

    public void setDrivetime(int drivetime) {
        this.drivetime = drivetime;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSpeedlimit() {
        return speedlimit;
    }

    public void setSpeedlimit(int speedlimit) {
        this.speedlimit = speedlimit;
    }

    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(toNodeNumber);
        objectOutputStream.writeInt(drivetime);
        objectOutputStream.writeInt(length);
        objectOutputStream.writeInt(speedlimit);
    }

    @Override
    public String toString() {
        return "Edge [drivetime=" + drivetime + ", length=" + length + ", speedlimit=" + speedlimit + ", to=" + toNodeNumber
                + "]";
    }
}

class Graph implements Serializable {
    private Node[] nodes;
    private PointsOfInterest pointsOfInterest;

    public Graph() {
        
    }

    public Graph(Node[] nodes) {
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public PointsOfInterest getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(PointsOfInterest pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public void reset() {
        for (Node node : nodes) {
            node.setDistance(Integer.MAX_VALUE);
            node.setEnqueued(false);
            node.setPrevious(-1);
            node.setVisited(false);
            node.setDriveTime(Integer.MAX_VALUE);
        }
    }
}

class GraphReader {
    public void parseNodeFile(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {        
            StreamReader reader = new StreamReader(stream);
            int numberOfNodes = reader.readNextInt();
            Node[] nodes = new Node[numberOfNodes];
            Node[] invertedNodes = new Node[numberOfNodes];
    
            for (int i = 0; i < numberOfNodes; i++) {
                if (i % (numberOfNodes / 100) == 0) {
                    System.out.printf("~ parsing nodes ~ : %d%%%n", i * 100 / numberOfNodes);
                }
                int index = reader.readNextInt();
                double latt = reader.readNextDouble();
                double longt = reader.readNextDouble();

                nodes[index] = new Node(latt, longt);
                invertedNodes[index] = new Node(latt, longt);
            }
    
            graph.setNodes(nodes);
            invertedGraph.setNodes(invertedNodes);
        }
    }

    public void parseEdgeFile(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {
            StreamReader reader = new StreamReader(stream);
            int numberOfEdges = reader.readNextInt();
            Node[] nodes = graph.getNodes();
            Node[] invertedNodes = invertedGraph.getNodes();
    
            for (int i = 0; i < numberOfEdges; i++) {
                if (i % (numberOfEdges / 100) == 0) {
                    System.out.printf("~ parsing edges ~ : %d%%%n", i * 100 / numberOfEdges);
                }
                int fromNodeNumber = reader.readNextInt();
                int toNodeNumber = reader.readNextInt();
                int drivetime = reader.readNextInt();
                int length = reader.readNextInt();
                int speedlimit = reader.readNextInt();
    
                Edge edge = new Edge(toNodeNumber, drivetime, length, speedlimit);
                Edge invertedEdge = new Edge(fromNodeNumber, drivetime, length, speedlimit);
                
                nodes[fromNodeNumber].getEdges().add(edge);
                invertedNodes[toNodeNumber].getEdges().add(invertedEdge);
            }
        }
    }

    public void parsePointsOfInterest(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {
            StreamReader reader = new StreamReader(stream);

            int pointsOfInterestCount = reader.readNextInt();
            PointsOfInterest pointsOfInterest = new PointsOfInterest(pointsOfInterestCount);

            for (int i = 0; i < pointsOfInterestCount; i++) {
                if (i % (pointsOfInterestCount / 100) == 0) {
                    System.out.printf("~ parsing POIs ~  : %d%%%n", i * 100 / pointsOfInterestCount);
                }
                int nodeNumber = reader.readNextInt();
                int categoryNumber = reader.readNextInt();
                PointOfInterestCategory category = PointOfInterestCategory.fromNumber(categoryNumber);
                String quotedName = reader.readNextUntilEndOfLine();
                String name = quotedName.substring(1, quotedName.length() - 1);
                pointsOfInterest.addPointOfInterest(nodeNumber, category, name);
            }

            graph.setPointsOfInterest(pointsOfInterest);
            invertedGraph.setPointsOfInterest(pointsOfInterest);
        }
    }
}

public class Main {
    private static void writeLatLonToFile(Graph graph, int nodeNumber) {
        try (PrintStream printStream = new PrintStream("cords.txt")) {    
            Node node = graph.getNodes()[nodeNumber];
            ArrayList<Node> nodes = new ArrayList<>();
            while (node != null) {
                nodes.add(node);
                node = node.getPrevious() == -1 ? null : graph.getNodes()[node.getPrevious()];
            }

            int stepSize = (int)Math.ceil(nodes.size() / 100.0);
            for (int i = 0; i < nodes.size(); i += stepSize) {
                printStream.println(nodes.get(i).getLatitude() + ", " + nodes.get(i).getLongitude());
            }
            printStream.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeNearestToFile(int[] stations, Graph graph) {
        try (PrintStream printStream = new PrintStream("nearest_cords.txt")) { 
            for(int i=0; i<stations.length; i++) {
                printStream.println(graph.getNodes()[stations[i]].getLatitude() + ", " + graph.getNodes()[stations[i]].getLongitude());
            } 
            printStream.println();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runDijkstra(Timer timer, Graph graph, String startNodeNumber, String endNodeNumber) {
        System.out.println("Dijkstra:");
        Dijkstra dijkstra = new Dijkstra(graph);
        int nodeNumber = dijkstra.searchToNode(timer, Integer.parseInt(startNodeNumber), Integer.parseInt(endNodeNumber));
        writeLatLonToFile(graph, nodeNumber);
        Node node = nodeNumber == -1 ? null : graph.getNodes()[nodeNumber];
        String distance = (node == null ? "not found" : Integer.toString(node.getDistance()));
        String driveTime = (node == null ? "not found" : driveTimeToString(node.getDriveTime()));
        System.out.println("  - Distance      : " + distance + "m");
        System.out.println("  - Dequeued nodes: " + dijkstra.getPrevPollCount());
        System.out.println("  - Drive time    : " + driveTime); 
        
        int count = 0;
        for (int i = nodeNumber; i != -1; i = graph.getNodes()[i].getPrevious()) {
            count++;
        }
        System.out.println("  - Nodes in route: " + count);
    }
    
    private static void runAlt(Timer timer, Graph graph, Graph invertedGraph, int[] landmarks, String rawStartNodeNumber, String rawEndNodeNumber){
        System.out.println("ALT:");
        int startNodeNumber = Integer.parseInt(rawStartNodeNumber);
        int endNodeNumber = Integer.parseInt(rawEndNodeNumber);
        ALT alt = new ALT(graph, invertedGraph, landmarks);
        int distance = alt.search(timer, startNodeNumber, endNodeNumber);
        System.out.println("  - Distance      : " + distance + "m");
        System.out.println("  - Dequeued nodes: " + alt.getPrevPollCount());
        System.out.println("  - Drive time    : " + driveTimeToString(graph.getNodes()[endNodeNumber].getDriveTime()));

        int count = 0;
        for (int i = endNodeNumber; i != -1; i = graph.getNodes()[i].getPrevious()) {
            count++;
        }
        System.out.println("  - Nodes in route: " + count);
    }

    private static String driveTimeToString(int time) {  
        int hours = time / 360000;
        time -= hours * 360000;
        int minutes = time / 6000;
        time -= minutes * 6000;
        int seconds = time / 100;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static void printDriveTime(Node node) {   
        int time = node.getDriveTime();
        System.out.println("Drivetime: \n" + driveTimeToString(time));
    }

    private static void printTimers(Timer... timer) {
        Timer time; 
        for (int i = 0; i < timer.length; i++) {
            time = timer[i];
            System.out.println(time.getName() + " time : " + time.getTimeSeconds() + " sec.");
        }
    }

    public static void main(String[] args) {
        Timer timerTotal = new Timer("TOTAL");
        timerTotal.start();
        if (args[0].equals("-h")){
            System.out.println(
                "'noder'_file 'kanter'_file 'interessepkt'_file '-c' startNodeNumber categoryNumber amount  // To find 10 gasstations or 5 charge stations etc.\n" +    
                "'noder'_file 'kanter'_file 'interessepkt'_file '-d' startNodeNumber endNodeNumber  // Dijkstra search from point to point\n" +
                "'noder'_file 'kanter'_file 'interessepkt'_file '-a' startNodeNumber endNodeNumber land1,land2,land3,land4,land5  // ALT search from point to point, specify landmarks e.g 702477,2151398,203356,5263302,5662488\n" +
                "'noder'_file 'kanter'_file 'interessepkt'_file '-da' startNodeNumber endNodeNumber land1,land2,land3,land4,land5  // Dijkstra and ALT search from point to point \n" +
                "'noder'_file 'kanter'_file 'interessepkt'_file '-h'  help "
            );
            return;
        }

        String nodeFilePath = args[0];
        String edgeFilePath = args[1];
        String POIsFilPath = args[2];
        int startNodeNumber = Integer.parseInt(args[4]);
        int endNodeNumber = Integer.parseInt(args[5]);
        int[] landmarks = null;
        if (args.length > 6) {
            landmarks = Arrays.stream(args[6].split(",")).mapToInt(Integer::parseInt).toArray();
        }
        
        GraphReader reader = new GraphReader();
        Graph graph = new Graph();
        Graph invertedGraph = new Graph();

        try {
            reader.parseNodeFile(graph, invertedGraph, nodeFilePath);
            reader.parseEdgeFile(graph, invertedGraph, edgeFilePath);
            reader.parsePointsOfInterest(graph, invertedGraph, POIsFilPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Timer timerDijkstra = new Timer("Dijkstra");
        Timer timerALT = new Timer("ALT");

        switch(args[3]) {
            case ("-c"):
            // Trondheim Lufthavn, Værnes - Node: 6590451
            // Røros hotell - Node: 1419364
                Dijkstra dijkstra = new Dijkstra(graph);
                int categoryNumber = endNodeNumber;
                int amount = Integer.parseInt(args[6]);
                int[] nearestPOIs = dijkstra.searchNearest(timerDijkstra, startNodeNumber, PointOfInterestCategory.fromNumber(categoryNumber), amount); 
                writeNearestToFile(nearestPOIs, graph);
                
                for(int i=0; i<nearestPOIs.length; i++) {
                    System.out.printf(
                        "%-30s | %-6dm | %-18s\n", 
                        graph.getPointsOfInterest().getPointOfInterest(nearestPOIs[i]).getName(),
                        graph.getNodes()[nearestPOIs[i]].getDistance(),
                        driveTimeToString(graph.getNodes()[nearestPOIs[i]].getDriveTime())
                    );
                }
                printTimers(timerDijkstra);
                break;
                
            case ("-d"):
                runDijkstra(timerDijkstra, graph, args[4], args[5]);
                printTimers(timerDijkstra);
                break;

            case ("-da"):
                runDijkstra(timerDijkstra, graph, args[4], args[5]);
                printTimers(timerDijkstra);
                
            case ("-a"):
                runAlt(timerALT, graph, invertedGraph, landmarks, args[4], args[5]);
                printTimers(timerALT);
                break;
                
            case ("-h"):
                System.out.println(" -c søker på nermeste stasjon til node\n -d søker med dijkstra gjennom graf\n -da kjører dijkstra og alt ");
            default: 
            
        }
        timerTotal.end();
        printTimers(timerTotal);
    }
}

class Node {
    private LinkedList<Edge> edges;
    private double latitude; //Breddegrad
    private double longitude; //Langdegrad

    private int previous;
    private boolean visited;
    private boolean enqueued;
    private int distance;
    private int driveTime;
    private int estimatedDistance = -1;

    public Node(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.edges = new LinkedList<Edge>();
    }

    public int findSumDistance() {
        return distance + estimatedDistance;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(int estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPrevious() {
        return previous;
    }

    public void setPrevious(int previousNodeNumber) {
        this.previous = previousNodeNumber;
    }

    public int getDriveTime() {
        return this.driveTime;
    }

    public void setDriveTime(int driveTime) {
        this.driveTime = driveTime;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isEnqueued() {
        return enqueued;
    }

    public void setEnqueued(boolean enqueued) {
        this.enqueued = enqueued;
    }

    public void serialize(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeDouble(latitude);
        objectOutputStream.writeDouble(longitude);
        for (Edge edge : edges) {
            edge.serialize(objectOutputStream);
        }
    }

    @Override
    public String toString() {
        return "Node []";
    }
}


class PointOfInterest {
    private final PointOfInterestCategory category;
    private final String name;

    public PointOfInterest(PointOfInterestCategory category, String name) {
        this.category = Objects.requireNonNull(category);
        this.name = Objects.requireNonNull(name);
    }

    public PointOfInterestCategory getCategory() {
        return category;
    }
    
    public String getName() {
        return name;
    }
}

enum PointOfInterestCategory {
    PLACE(1),
    GAS_STATION(2),
    CHARGING_STATION(4);

    private final int number;
    
    PointOfInterestCategory(int number) {
        this.number = number;
    }

    public static PointOfInterestCategory fromNumber(int number) {
        for (PointOfInterestCategory category : PointOfInterestCategory.values()) {
            if (category.number == number) {
                return category;
            }
        }
        throw new NoSuchElementException("category " + number + " does not exist");
    }
}

class PointsOfInterest {
    private final HashMap<Integer, PointOfInterest> pointsOfInterest;

    public PointsOfInterest(int capacity) {
        pointsOfInterest = new HashMap<>(capacity);
    }

    public void addPointOfInterest(int nodeNumber, PointOfInterestCategory category, String name) {
        pointsOfInterest.put(nodeNumber, new PointOfInterest(category, name));
    }

    public PointOfInterest getPointOfInterest(int nodeNumber) {
        return pointsOfInterest.get(nodeNumber);
    }
}

class StreamReader {
    private final InputStream stream;

    public StreamReader(InputStream stream) {
        this.stream = stream;
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private int readOrThrowEOF() throws IOException, EOFException {
        int c = stream.read();
        if (c == -1) {
            throw new EOFException();
        }
        return c;
    }

    private int skipSpaces() throws IOException {
        int c;
        while (Character.isWhitespace(c = readOrThrowEOF())) {
            stream.mark(1);
        }
        return c;
    }

    public int readNextInt() throws IOException {
        int c = skipSpaces();

        int mult = 1;
        if (c == '-') {
            mult = -1;
            c = readOrThrowEOF();
        }

        int result = 0;
        do {
            int digit = (c - '0');
            result = (result * 10) + digit;
        } while (isDigit(c = readOrThrowEOF()));

        return result * mult;
    }

    public double readNextDouble() throws IOException {
        int c = skipSpaces();

        int mult = 1;
        if (c == '-') {
            mult = -1;
            c = readOrThrowEOF();
        }

        long integer = 0;
        long decimal = 0;
        double decimalMult = 1;
        do {
            int digit = (c - '0');
            integer = (integer * 10) + digit;
        } while(isDigit(c = readOrThrowEOF()));
        if (c != '.') {
            return integer;
        }
        while (isDigit(c = readOrThrowEOF())) {
            int digit = (c - '0');
            decimal = (decimal * 10) + digit;
            decimalMult *= 0.1;
        }

        return ((double)integer + (decimal * decimalMult)) * mult;
    }

    public String readNextUntilEndOfLine() throws IOException {
        int c = skipSpaces();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(c);
        while ((c = readOrThrowEOF()) != '\n') {
            byteArrayOutputStream.write(c);
        }

        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }
}
class Timer {
    private String name;
    private long start;
    private long end;

    public Timer(String name) {
        this.name = name;
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void end() {
        end = System.currentTimeMillis();
    }

    public float getTimeSeconds() {
        return (end - start) / 1000F;
    }

    public float getTimeMinutes() {
        return (end - start) / (60*1000F);
    }

    public float getTimeHours(){
        return (end - start) / (60*60*1000F);
    }

    public String getName() {
        return name;
    }
}
