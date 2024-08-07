package spiderman;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 *      i.    a (int): number of dimensions in the graph
 *      ii.   b (int): the initial size of the cluster table prior to rehashing
 *      iii.  c (double): the capacity(threshold) used to rehash the cluster table 
 * 2. a lines, each with:
 *      i.    The dimension number (int)
 *      ii.   The number of canon events for the dimension (int)
 *      iii.  The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 *      i.    The dimension they are currently at (int)
 *      ii.   The name of the person (String)
 *      iii.  The dimensional signature of the person (int)
 * 
 * Step 3:
 * SpotInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * Two integers (line seperated)
 *      i.    Line one: The starting dimension of Spot (int)
 *      ii.   Line two: The dimension Spot wants to go to (int)
 * 
 * Step 4:
 * TrackSpotOutputFile name is passed in through the command line as args[3]
 * Output to TrackSpotOutputFile with the format:
 * 1. One line, listing the dimenstional number of each dimension Spot has visited (space separated)
 * 
 * @author Seth Kelley
 */

public class TrackSpot {
    
    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.TrackSpot <dimension INput file> <spiderverse INput file> <spot INput file> <trackspot OUTput file>");
                return;
        }

        // WRITE YOUR CODE HERE
        String dimList = args[0];
        String outputFile = args[3];
        String spotInput = args[2];

        Clusters clusters = readClusters(dimList);
        if (clusters == null) {
            System.out.println("Error reading clusters.");
            return;
        }

        HashMap<Integer, LinkedList<Integer>> adjList = createAdjList(clusters);
        if (adjList == null) {
            System.out.println("Error creating adjacency list.");
            return;
        }

        LinkedList<Integer> route = new LinkedList<>();
        int[] check = readSpot(spotInput); 
        DFS(adjList, check[0], check[1], route, new HashSet<>());
        printRoute(route, outputFile);
    }
    
    private static Clusters readClusters(String dimensionsFile) {
        StdIn.setFile(dimensionsFile);
        int dimensions = StdIn.readInt();
        int size = StdIn.readInt();
        double capacity = StdIn.readDouble(); 
        Clusters cluster12 = new Clusters(size, capacity);
        for( int i = 0; i<dimensions;i++) {
            int dimensionNumber = StdIn.readInt();
            int canonEvents = StdIn.readInt();
            int dimensionWeight = StdIn.readInt(); 
            cluster12.insertDim(dimensionNumber,capacity,i);
        }
        cluster12.wrap(); 
        return cluster12; 
    }

    public static int[] readSpot(String dim){
        StdIn.setFile(dim);
        int [] start_end = new int [2]; 
        int start = StdIn.readInt(); 
        int end = StdIn.readInt(); 
        start_end[0]= start;
        start_end[1]=end;
        return start_end; 

    }
    private static HashMap<Integer, LinkedList<Integer>> createAdjList(Clusters clusters) {
        if (clusters == null) {
            System.out.println("Clusters object is null.");
            return null;
        }

        HashMap<Integer, LinkedList<Integer>> adjacencyList = new HashMap<>();
        LinkedList<Integer>[] hashTableFromClusters = clusters.getClusters();

        if (hashTableFromClusters == null) {
            System.out.println("Hash table from clusters is null.");
            return null;
        }

        for (LinkedList<Integer> cluster : hashTableFromClusters) {
            if (cluster != null && !cluster.isEmpty()) {
                int one = cluster.getFirst();
                LinkedList<Integer> Connect = new LinkedList<>();
                for (int j = 1; j < cluster.size(); j++) {
                    int next = cluster.get(j);
                    Connect.add(next);
                    adjacencyList.computeIfAbsent(next, k -> new LinkedList<>()).add(one);
                }
                adjacencyList.put(one, Connect);
            }
        }
        return adjacencyList;
    }


    public static void DFS(HashMap<Integer, LinkedList<Integer>> graph,int start, int end, List<Integer> route, Set<Integer> visited){
        route.add(start);
        visited.add(start);
        System.out.print(start + " "); // Debugging statement
        if( start == end){
            return;
        }
        for (int neighbor : graph.getOrDefault(start, new LinkedList<>())) {
            if (!visited.contains(neighbor)) {
                System.out.print(neighbor + " " ); // Debugging statement
                DFS(graph, neighbor, end, route, visited);
                if (route.get(route.size() - 1) == end) return; // This condition should terminate the DFS if the end dimension is reached
            }
        }
     }

     private static void printRoute(List<Integer> route, String outputFile) {
        StdOut.setFile(outputFile);
        for (int dimension : route) {
            StdOut.print(dimension + " ");
        }
        StdOut.println();
    }        
}
