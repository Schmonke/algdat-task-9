import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    
    public static void parseNodeFile(Graph graph, Graph invertedGraph, String path, Scanner scanner) {
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

            nodes[i] = new Node(number, null, false, Integer.MAX_VALUE, latt, longt, new LinkedList<Edge>());
            invertedNodes[i] = new Node(number, null, false, Integer.MAX_VALUE, latt, longt, new LinkedList<Edge>());
        }

        graph.setNodes(nodes);
        invertedGraph.setNodes(invertedNodes);
    }

    // "This will be easy"
    // - Magnus 2021-11-14T19:14

    // Tis Was Easyz

    public static void parseEdgeFile(Graph graph, Graph invertedGraph, String path, Scanner scanner) {
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

            edge = new Edge(nodes[toNodeNumber], drivetime, length, speedlimit);
            invertedEdge = new Edge(invertedNodes[fromNodeNumber], drivetime, length, speedlimit);
            
            nodes[fromNodeNumber].getEdges().add(edge);
            invertedNodes[toNodeNumber].getEdges().add(invertedEdge);
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

        Dijkstra dijkstra = new Dijkstra(graph.getNodes(), 0, 9);
        System.out.println(dijkstra.search());
        //Arrays.stream(graph.getNodes()).forEach(node -> System.out.println(node.getEdges().toString()));
    }
}
