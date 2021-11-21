import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private static int readOrThrowEOF(InputStream stream) throws IOException, EOFException {
        int c = stream.read();
        if (c == -1) {
            throw new EOFException();
        }
        return c;
    }

    private static int skipSpaces(InputStream stream) throws IOException {
        int c;
        while (Character.isWhitespace(c = readOrThrowEOF(stream))) {
            stream.mark(1);
        }
        return c;
    }

    private static int readNextInt(InputStream stream) throws IOException {
        int c = skipSpaces(stream);

        int mult = 1;
        if (c == '-') {
            mult = -1;
            c = readOrThrowEOF(stream);
        }

        int result = 0;
        do {
            int digit = (c - '0');
            result = (result * 10) + digit;
        } while (isDigit(c = readOrThrowEOF(stream)));

        return result * mult;
    }

    private static double readNextDouble(InputStream stream) throws IOException {
        int c = skipSpaces(stream);

        int mult = 1;
        if (c == '-') {
            mult = -1;
            c = readOrThrowEOF(stream);
        }

        long integer = 0;
        long decimal = 0;
        double decimalMult = 1;
        do {
            int digit = (c - '0');
            integer = (integer * 10) + digit;
        } while(isDigit(c = readOrThrowEOF(stream)));
        if (c != '.') {
            return integer;
        }
        while (isDigit(c = readOrThrowEOF(stream))) {
            int digit = (c - '0');
            decimal = (decimal * 10) + digit;
            decimalMult *= 0.1;
        }

        return ((double)integer + (decimal * decimalMult)) * mult;
    }

    private static void parseNodeFile(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {
            long timeStart = System.currentTimeMillis();
        
            int numberOfNodes = readNextInt(stream);
            Node[] nodes = new Node[numberOfNodes];
            Node[] invertedNodes = nodes.clone();
    
            int number;
            double latt;
            double longt;
    
            for (int i = 0; i < numberOfNodes; i++) {
                if (i % 1200000 == 0 || i > 6892650) System.out.println("~ parsing nodes ~ : " + (i+1));
                number = readNextInt(stream);
                latt = readNextDouble(stream);
                longt = readNextDouble(stream);

                nodes[i] = new Node(number, latt, longt);
                invertedNodes[i] = new Node(number, latt, longt);
            }
    
            graph.setNodes(nodes);
            invertedGraph.setNodes(invertedNodes);
            System.out.println("Time used : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));
        }
        /*long timeStart = System.currentTimeMillis();
        
        int numberOfNodes = Integer.parseInt(reader.nextLine().replace(" ", ""));
        Node[] nodes = new Node[numberOfNodes];
        Node[] invertedNodes = nodes.clone();

        int number;
        double latt;
        double longt;

        for (int i = 0; i < numberOfNodes; i++) {
            if (i % 1200000 == 0 || i > 6892650) System.out.println("~ parsing nodes ~ : " + (i+1));
            number = readNextInt(stream);
            latt = reader.nextDouble();
            longt = reader.nextDouble();

            nodes[i] = new Node(number, latt, longt);
            invertedNodes[i] = new Node(number, latt, longt);
        }

        graph.setNodes(nodes);
        invertedGraph.setNodes(invertedNodes);
        System.out.println("Time used : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));*/
    }

    // "This will be easy"
    // - Magnus 2021-11-14T19:14

    // Tis Was Easyz

    private static void parseEdgeFile(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {
            long timeStart = System.currentTimeMillis();
            int numberOfEdges = readNextInt(stream);
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
                fromNodeNumber = readNextInt(stream);
                toNodeNumber = readNextInt(stream);
                drivetime = readNextInt(stream);
                length = readNextInt(stream);
                speedlimit = readNextInt(stream);
    
                edge = new Edge(toNodeNumber, drivetime, length, speedlimit);
                invertedEdge = new Edge(fromNodeNumber, drivetime, length, speedlimit);
                
                nodes[fromNodeNumber].getEdges().add(edge);
                invertedNodes[toNodeNumber].getEdges().add(invertedEdge);
            }
            System.out.println("Time used : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));
        }
        /*long timeStart = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new File(path));
        int numberOfEdges = readNextInt(stream);
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
            fromNodeNumber = readNextInt(stream);
            toNodeNumber = readNextInt(stream);
            drivetime = readNextInt(stream);
            length = readNextInt(stream);
            speedlimit = readNextInt(stream);

            edge = new Edge(toNodeNumber, drivetime, length, speedlimit);
            invertedEdge = new Edge(fromNodeNumber, drivetime, length, speedlimit);
            
            nodes[fromNodeNumber].getEdges().add(edge);
            invertedNodes[toNodeNumber].getEdges().add(invertedEdge);
        }
        System.out.println("Time used : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));*/
    }

    public static void main(String[] args) throws IOException {
        long timeStart = System.currentTimeMillis();
        String nodefilePath = args[0];
        String edgefilePath = args[1];
        int[] landmarks = {100, 200, 300, 400, 500};

        Graph graph = new Graph();
        Graph invertedGraph = new Graph();
        parseNodeFile(graph, invertedGraph, nodefilePath);
        parseEdgeFile(graph, invertedGraph, edgefilePath);

        Dijkstra dijkstra = new Dijkstra(graph);
        int dijkstraDistance = dijkstra.search(0, 9);
        System.out.println(dijkstraDistance);
        //Arrays.stream(graph.getNodes()).forEach(node -> System.out.println(node.getEdges().toString()));
        
        ALT alt = new ALT(graph, invertedGraph, landmarks);
        int altDistance = alt.search(0, 9);
        System.out.println(altDistance);
        System.out.println("Time used total : " + ((System.currentTimeMillis() - timeStart) / (60*1000F)));
    }
}