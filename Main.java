import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static int[] LAND = {702477, 2151398, 203356, 5263302, 5662488};

    private static void runDijkstra(Timer timer, Graph graph, String startNodeNumber, String endNodeNumber) {
        Dijkstra dijkstra = new Dijkstra(graph);
        timer.start();
        int nodeNumber = dijkstra.searchToNode(Integer.parseInt(startNodeNumber), Integer.parseInt(endNodeNumber));
        timer.end();
        Node node = graph.getNodes()[nodeNumber];

        while (node != null) {
            System.out.println(node.getLatitude() + ", " + node.getLongitude());
            node = node.getPrevious() == -1 ? null : graph.getNodes()[node.getPrevious()];
        }
        System.out.println(nodeNumber == -1 ? "not found" : graph.getNodes()[nodeNumber].getDistance());
        getDriveTime(graph.getNodes()[nodeNumber]);     
    }
    
    private static void runAlt(Timer timer, Graph graph, Graph invertedGraph, int[] landmarks, String startNodeNumber, String endNodeNumber){
        ALT alt = new ALT(graph, invertedGraph, landmarks);
        timer.start();
        int distance = alt.search(Integer.parseInt(startNodeNumber), Integer.parseInt(endNodeNumber));
        timer.end();
        System.out.println(distance);
    }

    private static void getDriveTime(Node node) {   
        double time = node.getDriveTime();
        System.out.println("Drivetime: " + time/(100*60*60) + "hours \nor " + time/(100*60) +"minutes \nor " + time/100 + "seconds");
    }

    private static void printTimers(Timer... timer) {
        Timer time; 
        for (int i = 0; i < timer.length; i++) {
            time = timer[i];
            System.out.println(time.getName() + ": " + time.getTimeSeconds() + " sec.");
        }
    }

    public static void main(String[] args) {
        Timer timerTotal = new Timer("TOTAL");
        timerTotal.start();

        String nodefilePath = args[1];
        String edgefilePath = args[2];
        int startNodeNumber = Integer.parseInt(args[3]);
        int endNodeNumber = Integer.parseInt(args[4]);
        int[] landmarks;
        if (args[5] != null) {
            landmarks = Arrays.stream(args[5].split(",")).mapToInt(Integer::parseInt).toArray();

        }
        
        GraphReader reader = new GraphReader();
        
        Graph graph = new Graph();
        Graph invertedGraph = new Graph();

        try {
            reader.parseNodeFile(graph, invertedGraph, nodefilePath);
            reader.parseEdgeFile(graph, invertedGraph, edgefilePath);
            reader.parsePointsOfInterest(graph, invertedGraph, "norden/interessepkt.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Timer timerDijkstra = new Timer("Dijkstra");
        Timer timerALT = new Timer("ALT");

        switch(args[0]) {
            case ("-c"):
                Dijkstra dijkstra = new Dijkstra(graph);
                int[] gasstation = dijkstra.searchNearest(6490451, PointOfInterestCategory.GAS_STATION, 10); // Trondheim Lufthavn, Værnes - Node: 6590451
                int[] chargestation = dijkstra.searchNearest(1419364, PointOfInterestCategory.CHARGING_STATION, 10); // Røros hotell - Node: 1419364
                
                for(int i=0; i<gasstation.length; i++) {
                    System.out.println(graph.getPointsOfInterest().getPointOfInterest(gasstation[i]).getName() + " - " + graph.getNodes()[gasstation[i]].getDistance() + "km");
                }
                printTimers(timerDijkstra, timerTotal);
                break;
            case ("-d"):
                runDijkstra(timerDijkstra, graph, args[3], args[4]);
                printTimers(timerDijkstra);
                break;

            case ("-a"):
                runAlt(timerALT, graph, invertedGraph, landmarks, args[3], args[4]);
                printTimers();
                break;
            case ("-da"):
                runDijkstra(timerDijkstra, graph, args[3], args[4]);
                runAlt(timerALT, graph, invertedGraph, landmarks, args[3], args[4]);
                printTimers(timerDijkstra, timerALT);
                break;
            case ("-h"):
                System.out.println(" -c, -d, -da, ");
            default: 
        }
        timerTotal.end();
        printTimers(timerTotal);
    }
}