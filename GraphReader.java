import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class GraphReader {
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