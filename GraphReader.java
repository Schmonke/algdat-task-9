import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class GraphReader {
    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private int readOrThrowEOF(InputStream stream) throws IOException, EOFException {
        int c = stream.read();
        if (c == -1) {
            throw new EOFException();
        }
        return c;
    }

    private int skipSpaces(InputStream stream) throws IOException {
        int c;
        while (Character.isWhitespace(c = readOrThrowEOF(stream))) {
            stream.mark(1);
        }
        return c;
    }

    private int readNextInt(InputStream stream) throws IOException {
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

    private double readNextDouble(InputStream stream) throws IOException {
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

    public void parseNodeFile(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {        
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
        }
    }

    public void parseEdgeFile(Graph graph, Graph invertedGraph, String path) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream stream = new BufferedInputStream(fileInputStream);
        ) {
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
                if (i % 3000000 == 0 || i > 15494450) System.out.println("~ parsing edges ~ : " + (i+1));
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
        }
    }

}
