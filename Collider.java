package spiderman;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Formatter;
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
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 *       all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {
    private String dimensionsListInputFile;
    private String spiderverseInputFile;
    private String outputFile;

    public Collider(String dimensionsListInputFile, String spiderverseInputFile, String outputFile) 
    {
        this.dimensionsListInputFile = dimensionsListInputFile;
        this.spiderverseInputFile = spiderverseInputFile;
        this.outputFile = outputFile;
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -cp bin spiderman.Collider <dimensionInputFile> <spiderverseInputFile> <colliderOutputFile>");
            return;
        }

        //WRITE YOUR CODE HERE
        String dimensionsListInputFile = args[0];
        String spiderverseInputFile = args[1];
        String outputFile = args[2];

        Collider collider = new Collider(dimensionsListInputFile, spiderverseInputFile, outputFile);

        Clusters clusters = readClusters(dimensionsListInputFile);
        HashMap<Integer, LinkedList<Integer>> adjacencyList = createAdjacencyList(clusters);

        collider.insertPeopleFromSpiderverse(adjacencyList, spiderverseInputFile);
        collider.printAdjList(adjacencyList, outputFile);
    }

    public static Clusters readClusters(String dimensionsListInputFile) {
    try {
        StdIn.setFile(dimensionsListInputFile);

        // Read total number of dimensions
        int dimensionNumber = StdIn.readInt();
        
        // Read table size and threshold
        int tableSize = StdIn.readInt();
        double threshold = StdIn.readDouble();

        // Initialize Clusters object
        Clusters clusters = new Clusters(tableSize, threshold);
        
        // Iterate over each dimension
        for (int i = 0; i < dimensionNumber; i++) {
            // Read individual dimension number, canon events, and dimension weight
            int dimNum = StdIn.readInt();
            int canonEvents = StdIn.readInt();
            int dimensionWeight = StdIn.readInt();
            
            // Insert dimension into clusters
            clusters.insertDim(dimNum, threshold, i);
        }
        
        // Perform wrapping
        clusters.wrap();
        
        // Return the populated Clusters object
        return clusters;
    } catch (Exception e) {
        // Handle any exceptions
        System.out.println("An error occurred while reading clusters: " + e.getMessage());
        e.printStackTrace(); // Print the stack trace for detailed error information
        return null; // Return null to indicate failure
    }
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
    
    
    

    public void insertPeopleFromSpiderverse(HashMap<Integer, LinkedList<Integer>> adjacencyList, String spiderverseInputFile) 
    {
        StdIn.setFile(spiderverseInputFile);
        int d = StdIn.readInt();

        for (int i = 0; i < d; i++) 
        {
            int dimensionNumber = StdIn.readInt();
            String nameOfP = StdIn.readString();
            int dimSignatureOfP = StdIn.readInt();

            if (adjacencyList.containsKey(dimensionNumber)) 
            {
                adjacencyList.get(dimensionNumber).add(dimSignatureOfP);
            }
        }
    }

    public void printAdjList(HashMap<Integer, LinkedList<Integer>> adjacencyList, String outputFile) {
        try (Formatter formatter = new Formatter(outputFile)) {
            for (Integer dimension : adjacencyList.keySet()) {
                LinkedList<Integer> currentList = adjacencyList.get(dimension);
                formatter.format("%d ", dimension);
                for (Integer connectedDimension : currentList) {
                    formatter.format("%d ", connectedDimension);
                }
                formatter.format("%n");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while writing to the output file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}