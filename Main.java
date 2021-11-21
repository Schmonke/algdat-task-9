import java.io.IOException;

public class Main {
    
    public static void main(String[] args) {
        Timer timerTotal = new Timer();
        timerTotal.start();

        Timer timerDijkstra = new Timer();
        Timer timerALT = new Timer();
        Timer timerALTConstructor = new Timer();

        String nodefilePath = args[0];
        String edgefilePath = args[1];
        int[] landmarks = {100, 200, 300, 400, 500};

        GraphReader reader = new GraphReader();
        
        Graph graph = new Graph();
        Graph invertedGraph = new Graph();
        try {
            reader.parseNodeFile(graph, invertedGraph, nodefilePath);
            reader.parseEdgeFile(graph, invertedGraph, edgefilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Dijkstra dijkstra = new Dijkstra(graph);
        timerDijkstra.start();
        int dijkstraDistance = dijkstra.search(6861306, 2518118);
        timerDijkstra.end();
        System.out.println(dijkstraDistance);
        //Arrays.stream(graph.getNodes()).forEach(node -> System.out.println(node.getEdges().toString()));
        

        timerALTConstructor.start();
        ALT alt = new ALT(graph, invertedGraph, landmarks);
        timerALTConstructor.end();
        timerALT.start();
        int altDistance = alt.search(6861306, 2518118);
        timerALT.end();
        System.out.println(altDistance);
        timerTotal.end();

        //Timeprints:
        System.out.println("Dijkstra time  : " + timerDijkstra.getTimeSeconds());
        System.out.println("ALT time       : " + timerALT.getTimeSeconds());
        System.out.println("ALT.ctor time  : " + timerALTConstructor.getTimeSeconds());
        System.out.println("TOTAL time     : " + timerTotal.getTimeSeconds());

    }
}