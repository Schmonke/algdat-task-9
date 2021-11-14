import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    
    public static void nodefileParser(Graph graph, String path, Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cant be null");
        int numberOfNodes = Integer.parseInt(scanner.nextLine().replace(" ", ""));
        Node[] nodes = new Node[numberOfNodes];

        int number;
        double latt;
        double longt;
        Node node;

        for (int i = 0; i < numberOfNodes; i++) {
            number = scanner.nextInt();
            latt = scanner.nextDouble();
            longt = scanner.nextDouble();

            node = new Node(number, null, false, Integer.MAX_VALUE, latt, longt, new LinkedList<Edge>());
            nodes[i] = node;
        }

        graph.setNodes(nodes);
    }

    public static void edgefileParser(Graph graph, String path, Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cant be null");
        int numberOfEdges = Integer.parseInt(scanner.nextLine().replace(" ", ""));
        Node[] nodes = graph.getNodes();

        int fromNodeNumber;
        int toNodeNumber;
        int drivetime;
        int length;
        int speedlimit;
        Edge edge;

        for (int i = 0; i < numberOfEdges; i++) {
            fromNodeNumber = scanner.nextInt();
            toNodeNumber = scanner.nextInt();
            drivetime = scanner.nextInt();
            length = scanner.nextInt();
            speedlimit = scanner.nextInt();

            edge = new Edge(nodes[toNodeNumber], drivetime, length, speedlimit);
            nodes[fromNodeNumber].getEdges().add(edge);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Graph graph = new Graph();
        Main.nodefileParser(graph, nodefilePath, nodeScanner);
        Main.edgefileParser(graph, edgefilePath, edgeScanner);

        //Arrays.stream(graph.getNodes()).forEach(node -> System.out.println(node.getEdges().toString()));
    }
}
