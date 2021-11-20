import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    
    private static void parseNodeFile(Graph graph, Graph invertedGraph, String path, Scanner scanner) {
        long timeStart = System.currentTimeMillis();
        Objects.requireNonNull(scanner, "Scanner cant be null");
        int numberOfNodes = Integer.parseInt(scanner.nextLine().replace(" ", ""));
        Node[] nodes = new Node[numberOfNodes];
        Node[] invertedNodes = nodes.clone();

        int number;
        double latt;
        double longt;

        for (int i = 0; i < numberOfNodes; i++) {
            if (i % 1200000 == 0 || i > 6892650) System.out.println("~ parsing nodes ~ : " + (i+1));
            number = scanner.nextInt();
            latt = scanner.nextDouble();
            longt = scanner.nextDouble();

            nodes[i] = new Node(number, latt, longt);
            invertedNodes[i] = new Node(number, latt, longt);
        }

        graph.setNodes(nodes);
        invertedGraph.setNodes(invertedNodes);
        System.out.println("Time used : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));
    }

    // "This will be easy"
    // - Magnus 2021-11-14T19:14

    // Tis Was Easyz

    private static void parseEdgeFile(Graph graph, Graph invertedGraph, String path, Scanner scanner) {
        long timeStart = System.currentTimeMillis();
        Objects.requireNonNull(scanner, "Scanner cant be null");
        int numberOfEdges = Integer.parseInt(scanner.nextLine().replace(" ", ""));
        Node[] nodes = graph.getNodes();
        Node[] invertedNodes = invertedGraph.getNodes();
        int fromNodeNumber;
        int toNodeNumber;
        int drivetime;
        int length;
        int speedlimit;
        Edge edge;
        Edge invertedEdge;

        for (int i = 0; i < numberOfEdges; i++) {
            if (i % 3000000 == 0 || i > 15494450) System.out.println("~ parsing edges ~" + (i+1));
            fromNodeNumber = scanner.nextInt();
            toNodeNumber = scanner.nextInt();
            drivetime = scanner.nextInt();
            length = scanner.nextInt();
            speedlimit = scanner.nextInt();

            edge = new Edge(toNodeNumber, drivetime, length, speedlimit);
            invertedEdge = new Edge(fromNodeNumber, drivetime, length, speedlimit);
            
            nodes[fromNodeNumber].getEdges().add(edge);
            invertedNodes[toNodeNumber].getEdges().add(invertedEdge);
        }
        System.out.println("Time used : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));
    }

    private static void preprocess(Graph graph, Graph invertedGraph, int[] landmarkNodeNumbers, String path, boolean test) {
        if (test) {
            ALTPreprocessor preprocessor = new ALTPreprocessor(graph, invertedGraph, landmarkNodeNumbers);
            preprocessor.preprocess(path);
            // ALT alt = new ALT(landmarkNodeNumbers.length, graph.getNodes().length, path);
            // for (int i = 0; i < landmarkNodeNumbers.length; i++) {
            //     System.out.print(alt.getFromLandmarkToNode()[i][new Random().nextInt(100000)] + " ");
            //     System.out.println(Arrays.equals(alt.getFromLandmarkToNode()[i], preprocessor.getFromLandmark()[i]));
            // }
        }
    }

    public static void main(String[] args) {
        long timeStart = System.currentTimeMillis();
        String nodefilePath = args[0];
        String edgefilePath = args[1];
        int[] landmarks = {100, 200, 300, 400, 500};
        String preprocessedFilePath = "./preprocessed_test_data.txt";

        Scanner nodeScanner = null;
        Scanner edgeScanner = null;
        try {
            nodeScanner = new Scanner(new File(nodefilePath));
            edgeScanner = new Scanner(new File(edgefilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Graph graph = new Graph();
        Graph invertedGraph = new Graph();
        parseNodeFile(graph, invertedGraph, nodefilePath, nodeScanner);
        parseEdgeFile(graph, invertedGraph, edgefilePath, edgeScanner);

        Dijkstra dijkstra = new Dijkstra(graph);
        int dijkstraDistance = dijkstra.search(0, 15);
        System.out.println(dijkstraDistance);
        //Arrays.stream(graph.getNodes()).forEach(node -> System.out.println(node.getEdges().toString()));

        preprocess(graph, invertedGraph, landmarks, preprocessedFilePath, false); //Set true to test

        ALT alt = new ALT(landmarks.length, graph.getNodes().length, preprocessedFilePath);
        int altDistance = alt.search(graph, 0, 15, landmarks);
        System.out.println(altDistance);
        System.out.println("Time used total : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));
    }
}