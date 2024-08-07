package spiderman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
 * Read from the SpotInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * AnomaliesInputFile name is passed through the command line as args[3]
 * Read from the AnomaliesInputFile with the format:
 * 1. e (int): number of anomalies in the file
 * 2. e lines, each with:
 *      i.   The Name of the anomaly which will go from the hub dimension to their home dimension (String)
 *      ii.  The time allotted to return the anomaly home before a canon event is missed (int)
 * 
 * Step 5:
 * ReportOutputFile name is passed in through the command line as args[4]
 * Output to ReportOutputFile with the format:
 * 1. e Lines (one for each anomaly), listing on the same line:
 *      i.   The number of canon events at that anomalies home dimensionafter being returned
 *      ii.  Name of the anomaly being sent home
 *      iii. SUCCESS or FAILED in relation to whether that anomaly made it back in time
 *      iv.  The route the anomaly took to get home
 * 
 * @author Seth Kelley
 */

public class GoHomeMachine {
    
    public static void main(String[] args) {

        if ( args.length < 5 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.GoHomeMachine <dimension INput file> <spiderverse INput file> <hub INput file> <anomalies INput file> <report OUTput file>");
                return;
        }

        // WRITE YOUR CODE HERE
        String dimensionsFilePath = args[0];
        String spiderverseFilePath = args[1];
        String hubFilePath = args[2];
        String anomaliesFilePath = args[3];
        String outputFilePath = args[4];
        Clusters clusters = readClusters(dimensionsFilePath);
        HashMap<Integer, LinkedList<Integer>> adjacencyList = createAdjacencyList(clusters);
        int startingHubDimension = readHub(hubFilePath);
        HashMap<Integer, String> spiderverseMap = readSpiderverse(spiderverseFilePath);
        List<String> anomaliesList = readAnomaliesInfo(anomaliesFilePath);
        List<String> report = processAnomalies(adjacencyList, startingHubDimension, spiderverseMap, anomaliesList);
        writeToReport(report, outputFilePath);
        
    }



            private static Clusters readClusters(String dimensionsFile) {
                StdIn.setFile(dimensionsFile);
                int numDimensions = StdIn.readInt();
                int clusterSize = StdIn.readInt();
                double clusterCapacity = StdIn.readDouble();
                Clusters clusters = new Clusters(clusterSize, clusterCapacity);
                for (int i = 0; i < numDimensions; i++) {
                    int dimensionNumber = StdIn.readInt();
                    int canonEvents = StdIn.readInt();
                    int dimensionWeight = StdIn.readInt();
                    clusters.insertDim(dimensionNumber, clusterCapacity, i);
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

                    private static int readHub(String hubFilePath) {
                        StdIn.setFile(hubFilePath);
                        return StdIn.readInt();
                    }
                    
                    private static HashMap<Integer, String> readSpiderverse(String spiderverseFilePath) {
                        StdIn.setFile(spiderverseFilePath);
                        int numberOfPeople = StdIn.readInt();
                        HashMap<Integer, String> spiderverseMap = new HashMap<>();
                        for (int i = 0; i < numberOfPeople; i++) {
                            int dimensionNumber = StdIn.readInt();
                            String personName = StdIn.readString();
                            int dimensionalSignature = StdIn.readInt();
                            spiderverseMap.put(dimensionNumber, personName);
                        }
                        return spiderverseMap;
                    }
                    


                            private static List<String> readAnomaliesInfo(String anomaliesFilePath) {
                                StdIn.setFile(anomaliesFilePath);
                                List<String> anomaliesInfo = new ArrayList<>();
                                int numberOfAnomalies = StdIn.readInt();
                                for (int i = 0; i < numberOfAnomalies; i++) {
                                    String anomalyName = StdIn.readString();
                                    int timeAllotted = StdIn.readInt();
                                    anomaliesInfo.add(anomalyName + " " + timeAllotted);
                                }
                                return anomaliesInfo;
                            }


                            private static List<String> processAnomalies(HashMap<Integer, LinkedList<Integer>> adjacencyList, int startingHub,
                            HashMap<Integer, String> spiderverse, List<String> anomalies) {
                                List<String> report = new ArrayList<>();
                                for (String anomaly : anomalies) {
                                String[] anomalyInfo = anomaly.split(" ");
                                String anomalyName = anomalyInfo[0];
                                int timeAllotted = Integer.parseInt(anomalyInfo[1]);
                                int dimensionOfAnomaly = getDimensionOfAnomaly(anomalyName, spiderverse);

                                // Check if anomaly is already at the hub
                                if (dimensionOfAnomaly == startingHub) {
                                report.add("0 " + anomalyName + " SUCCESS"); 
                                } else {
                                // Calculate the shortest path from hub to anomaly's dimension
                                List<Integer> shortestPath = calculateShortestPath(adjacencyList, startingHub, dimensionOfAnomaly);
                                String route = generateRoute(shortestPath);

                                // Check if the route is empty, indicating no path found
                                if (route.isEmpty()) {
                                report.add("0 " + anomalyName + " FAILED");
                                } else {
                                report.add("1 " + anomalyName + " SUCCESS " + route);
                                }
                                }
                                }
                                return report;
                                }


                                private static int getDimensionOfAnomaly(String anomalyName, HashMap<Integer, String> spiderverse) {
                                    // Iterate over the spiderverse entries
                                    for (Map.Entry<Integer, String> spider : spiderverse.entrySet()) {
                                        
                                        if (spider.getValue().equals(anomalyName)) {
                                            // Return the key (dimension) if found
                                            return spider.getKey();
                                        }
                                    }
                                    // Return -1 if anomalyName not found in spiderverse
                                    return -1;
                                }
                                

                                private static List<Integer> calculateShortestPath(HashMap<Integer, LinkedList<Integer>> adjList, int start, int end) {
                                    // Initialize data structures for Dijkstra's algorithm
                                    HashMap<Integer, Integer> distances = new HashMap<>();
                                    HashMap<Integer, Integer> prev = new HashMap<>();
                                    PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
                                    HashSet<Integer> visited = new HashSet<>();
                                
                                    // Initialize distances and prev maps
                                    for (Integer node : adjList.keySet()) {
                                        distances.put(node, Integer.MAX_VALUE);
                                        prev.put(node, null);
                                    }
                                    distances.put(start, 0);
                                    queue.offer(start);
                                
                                    // Perform Dijkstra's algorithm
                                    while (!queue.isEmpty()) {
                                        Integer current = queue.poll();
                                        visited.add(current);
                                        LinkedList<Integer> neighbors = adjList.get(current);
                                        if (neighbors != null) {
                                            for (Integer neighbor : neighbors) {
                                                if (!visited.contains(neighbor)) {
                                                    // Calculate new distance to neighbor
                                                    int alt = distances.get(current) + getDistance(current, neighbor);
                                                    // Update distance if shorter path found
                                                    if (alt < distances.get(neighbor)) {
                                                        distances.put(neighbor, alt);
                                                        prev.put(neighbor, current);
                                                        queue.offer(neighbor);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                
                                    // Reconstruct the shortest path from start to end
                                    LinkedList<Integer> path = new LinkedList<>();
                                    Integer current = end;
                                    while (current != null) {
                                        path.addFirst(current);
                                        current = prev.get(current);
                                    }
                                    return path;
                                }
                                
                                private static int getDistance(int current, int neighbor) {
                                    
                                    return 1; 
                                }

                                private static String generateRoute(List<Integer> shortestPath) {
                                    StringBuilder routeBuilder = new StringBuilder();
                                    for (Integer node : shortestPath) {
                                        routeBuilder.append(node).append(" ");
                                    }
                                    return routeBuilder.toString().trim();
                                }
                                
                                private static void writeToReport(List<String> report, String outputFile) {
                                    StdOut.setFile(outputFile);
                                    for (String line : report) {
                                        StdOut.println(line);
                                    }
                                }
                            
                            }
