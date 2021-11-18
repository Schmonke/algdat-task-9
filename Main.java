import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    
    private static void parseNodeFile(Graph graph, Graph invertedGraph, String path, Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cant be null");
        int numberOfNodes = Integer.parseInt(scanner.nextLine().replace(" ", ""));
        Node[] nodes = new Node[numberOfNodes];
        Node[] invertedNodes = nodes.clone();

        int number;
        double latt;
        double longt;

        for (int i = 0; i < numberOfNodes; i++) {
            number = scanner.nextInt();
            latt = scanner.nextDouble();
            longt = scanner.nextDouble();

            nodes[i] = new Node(number, latt, longt);
            invertedNodes[i] = new Node(number, latt, longt);
        }

        graph.setNodes(nodes);
        invertedGraph.setNodes(invertedNodes);
    }

    // "This will be easy"
    // - Magnus 2021-11-14T19:14

    // Tis Was Easyz

    private static void parseEdgeFile(Graph graph, Graph invertedGraph, String path, Scanner scanner) {
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
    }

    private static void preprocess(Graph graph, Graph invertedGraph, int[] landmarkNodeNumbers, boolean test) {
        if (test) {
            new ALTPreprocessor(graph, invertedGraph, landmarkNodeNumbers).preprocess();
        }
    }

    public static void main(String[] args) {
        String nodefilePath = args[0];
        String edgefilePath = args[1];

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
        dijkstra.search(0, -1);

        preprocess(graph, invertedGraph, new int[] {100, 200, 300, 400, 500}, false); //Set true to test
        //Arrays.stream(graph.getNodes()).forEach(node -> System.out.println(node.getEdges().toString()));
    }
}