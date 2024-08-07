package spiderman;
import java.util.*;

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
 * HubInputFile name is passed through the command line as args[2]
 * Read from the HubInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * CollectedOutputFile name is passed in through the command line as args[3]
 * Output to CollectedOutputFile with the format:
 * 1. e Lines, listing the Name of the anomaly collected with the Spider who
 *    is at the same Dimension (if one exists, space separated) followed by 
 *    the Dimension number for each Dimension in the route (space separated)
 * 
 * @author Seth Kelley
 */

public class CollectAnomalies {
    
    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.CollectAnomalies <dimension INput file> <spiderverse INput file> <hub INput file> <collected OUTput file>");
                return;
        }

        
        String dimensionsFileName = args[0];
        String spiderverseFileName = args[1];
        String hubFileName = args[2];
        String outputFileName = args[3];
        Clusters clusters = readClusters(dimensionsFileName);
        HashMap<Integer, LinkedList<Integer>> adjacencyList = createAdjacencyList(clusters);
        int hubDimension = readHub(hubFileName);
        HashMap<Integer, String> spiderverseMap = readSpiderverse(spiderverseFileName);
        List<String> routes = findRoutes(adjacencyList, hubDimension, spiderverseMap);
        printRoutes(routes, outputFileName);
    }

                private static Clusters readClusters(String dimensionsFileName) {
                    StdIn.setFile(dimensionsFileName);
                    int numDimensions = StdIn.readInt();
                    int initialSize = StdIn.readInt();
                    double threshold = StdIn.readDouble();
                    Clusters clusters = new Clusters(initialSize, threshold);
                    for (int i = 0; i < numDimensions; i++) {
                        int dimensionNumber = StdIn.readInt();
                        int canonEvents = StdIn.readInt();
                        int dimensionWeight = StdIn.readInt();
                        clusters.insertDim(dimensionNumber, threshold, i);
                    }
                    clusters.wrap();
                    return clusters;
                }
                

                    public static HashMap<Integer, LinkedList<Integer>> createAdjacencyList(Clusters clusters) {
                        HashMap<Integer, LinkedList<Integer>> adjacencyList = new HashMap<>();
                    
                        LinkedList<Integer>[] clustersArray = clusters.getClusters();
                    
                        for (LinkedList<Integer> cluster : clustersArray) {
                            int one = cluster.get(0);
                            LinkedList<Integer> Connect = new LinkedList<>();
                            for (int i = 1; i < cluster.size(); i++) {
                                int o = cluster.get(i);
                                Connect.add(o);
                                adjacencyList.computeIfAbsent(o, k -> new LinkedList<>()).add(one);
                            }
                            adjacencyList.put(one, Connect);
                        }
                        return adjacencyList;
                    }




                                private static int readHub(String hubFile) {
                                    StdIn.setFile(hubFile);
                                    return StdIn.readInt();
                                }
                                
                                private static HashMap<Integer, String> readSpiderverse(String spiderverseFile) {
                                    StdIn.setFile(spiderverseFile);
                                    int d = StdIn.readInt();
                                    HashMap<Integer, String> spiderverse = new HashMap<>();
                                    for (int i = 0; i < d; i++) {
                                        int dimension = StdIn.readInt(); 
                                        String nameOfPerson = StdIn.readString();
                                        int dimSignatureOfPerson = StdIn.readInt(); 
                                        spiderverse.put(dimension, nameOfPerson); 
                                    }
                                    return spiderverse;
                                }
                    

                                private static List<String> findRoutes(HashMap<Integer, LinkedList<Integer>> adjList, int startingHub, HashMap<Integer, String> spiderverse) {
                                    List<String> routes = new ArrayList<>();
                                    Queue<Integer> queue = new LinkedList<>();
                                    Set<Integer> visited = new HashSet<>();
                                    HashMap<Integer, Integer> parent = new HashMap<>();
                                
                                    queue.offer(startingHub);
                                    visited.add(startingHub);
                                    parent.put(startingHub, -1); // Mark hub's parent as -1
                                
                                    while (!queue.isEmpty()) {
                                        int current = queue.poll();
                                        LinkedList<Integer> neighbors = adjList.get(current);
                                        if (neighbors != null) {
                                            for (int neighbor : neighbors) {
                                                if (!visited.contains(neighbor)) {
                                                    visited.add(neighbor);
                                                    parent.put(neighbor, current); // Mark neighbor's parent as current
                                
                                                    String anomaly = spiderverse.get(neighbor);
                                                    String spider = spiderverse.get(current);
                                
                                                    if (anomaly != null && !anomaly.equals(spider)) {
                                                        // Backtrack to construct the route
                                                        List<Integer> route = new ArrayList<>();
                                                        int node = neighbor;
                                                        while (node != -1) {
                                                            route.add(node);
                                                            node = parent.get(node);
                                                        }
                                
                                                        // Construct the route string
                                                        StringBuilder routeStr = new StringBuilder();
                                                        for (int i = route.size() - 1; i >= 0; i--) {
                                                            routeStr.append(route.get(i)).append(" ");
                                                        }
                                                        routes.add(anomaly + " " + spider + " " + routeStr.toString().trim());
                                                    } else {
                                                        queue.offer(neighbor); // Add the neighbor to the queue for further exploration
                                                    }
                                                }
                                            }
                                        }
                                    }
                                
                                    return routes;
                                }
                                
                                private static void printRoutes(List<String> routes, String outputFile) {
                                    StdOut.setFile(outputFile);
                                    for (String route : routes) {
                                        StdOut.println(route);
                                    }
                                }
                            }                                