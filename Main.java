import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static int[] LAND = {702477, 2151398, 203356, 5263302, 5662488};

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
    }

    private static String driveTimeToString(int time) {  
        int hours = time / 360000;
        time -= hours * 360000;
        int minutes = time / 6000;
        time -= minutes * 6000;
        double seconds = time / 100.0;

        return String.format("%dh %dm %.2fs", hours, minutes, seconds);
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
                "'noder'_file 'kanter'_file 'interessepkt'_file '-a' startNodeNumber endNodeNumber land1,land2,land3,land4,land5  // ALT search from point to point\n" +
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