package spiderman;
import java.util.LinkedList;
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
 * 
 * Step 2:
 * ClusterOutputFile name is passed in through the command line as args[1]
 * Output to ClusterOutputFile with the format:
 * 1. n lines, listing all of the dimension numbers connected to 
 *    that dimension in order (space separated)
 *    n is the size of the cluster table.
 * 
 * @author Seth Kelley
 */

public class Clusters {
    private LinkedList<Integer>[] clusters;  //an array of linked lists to store the dimensions in each cluster
    private LinkedList<Integer>[] hashTable;
    public  int tableSize; //size of the cluster tables
    public  double capacity; //capacity or threshold used to rehash the cluster table.
    public int canonEvents; 
    public int dimensionWeight;
    public int dimensionNumber; 


    


    @SuppressWarnings("unchecked")
    public Clusters(int size, double capacity){
        this.tableSize = size;  //sets the value of the instance variable 'tablesize' to the value passed as an argument to constructor 'size'.
        this.capacity = capacity;
        this.clusters = new LinkedList[size];
        this.hashTable = new LinkedList[size];
        for(int x = 0; x<size; x++){
            clusters[x]= new LinkedList<>(); //'Linkedlist<>' basically creates a new expty linked list for every element. this iterates 'size' times, creating a new linked list for each cluster. 
            hashTable[x] = new LinkedList<>(); // Initialize each element of hashTable
        }
    }

        







        public  void insertDim(int dimension, double capacity, int count){
            
            if((count/tableSize)>=capacity) {  //This condition checks if the current load factor (ratio of count to tableSize) is greater than or equal to the specified capacity threshold. If the condition is true, it means that the cluster table is becoming too full, and it needs to be rehashed to prevent exceeding the capacity threshold. 
                reHash(tableSize);
            }
            int set = (dimension%tableSize);// This line calculates the hash value for the given dimension by taking the modulus (%) of the dimension with the tableSize. This operation ensures that the hash value falls within the range of the cluster table indices. The result (set) determines which cluster (bucket) the dimension should be inserted into.
            
            clusters[set].addFirst(dimension); //Once the hash value (set) is calculated, the dimension is added to the appropriate cluster represented by the set index in the clusters array. The addFirst() method is used to add the dimension to the beginning of the linked list representing the cluster. This is typically done to maintain constant-time complexity for insertion regardless of the number of elements already in the cluster.

        }




        @SuppressWarnings("unchecked")
            private void reHash(int size){
                int NTableSize = size*2; //This line calculates the new size for the hash table by doubling the current size (size * 2). This is a common strategy for resizing hash tables during rehashing.
                
                LinkedList<Integer>[] newClusters = new LinkedList[NTableSize]; //This line creates a new array of linked lists (LinkedList<Integer>[]) to serve as the new hash table. The size of the array is set to the new table size (NTableSize), and each element of the array is initialized with a new empty linked list.
                for (int i = 0; i < NTableSize; i++) {
                    newClusters[i] = new LinkedList<Integer>(); //The code iterates over each cluster in the existing hash table (clusters), and for each element (data) in each cluster, it calculates a new index (newIndex) based on the new table size (NTableSize).
                }

                
                for (LinkedList<Integer> cluster : clusters) { //cluster is an element of a linked list of integer type, iterating over the 'clusters' array.
                    for (int data : cluster) { //it means that the variable 'data' is iterating over every element with the specific linked list 'cluster'.
                        int newIndex = data % NTableSize;  //The code iterates over each cluster in the existing hash table (clusters), and for each element (data) in each cluster, it calculates a new index (newIndex) based on the new table size (NTableSize).
                        newClusters[newIndex].addFirst(data); // It then adds the element to the appropriate cluster in the new hash table (newClusters) by adding it to the front of the linked list at the calculated index. This ensures that elements are rehashed and redistributed properly into the new hash table.
                    }
                }

            
                clusters = newClusters; //updated to point to the new hash table.
                tableSize = NTableSize; //updated to reflect the new size of the hash table after rehashing.
            } 



            @SuppressWarnings("unchecked")
                public void wrap() {
                    int size= clusters.length;
                
                    
                    for( int x=0;x<size;x++){
                        if(x==0){
                            clusters[x].add(clusters[size-1].getFirst()); //The first element of the first cluster is set to the last element of the last cluster (clusters[size - 1].getFirst()).
                            clusters[x].add(clusters[size-2].getFirst()); //The second element of the first cluster is set to the second-to-last element of the last cluster (clusters[size - 2].getFirst()).
                        }else if(x==1){
                            clusters[x].add(clusters[0].getFirst()); //The first element of the second cluster is set to the first element of the first cluster (clusters[0].getFirst()).
                            clusters[x].add(clusters[size-1].getFirst()); //The second element of the second cluster is set to the last element of the last cluster (clusters[size - 1].getFirst()).

                        }else{
                            clusters[x].add(clusters[x-1].getFirst()); //The first element of the current cluster (clusters[x]) is set to the first element of the previous cluster (clusters[x - 1].getFirst()).
                            clusters[x].add(clusters[x-2].getFirst()); //The second element of the current cluster is set to the second element of the previous cluster (clusters[x - 2].getFirst()).
                            
                        }


                    }
                    
                }
                    
                
                
                    public void printClusters(){
                            for (LinkedList<Integer> list : clusters) { //This enhanced for loop iterates over each element (list) in the clusters array. Each element represents a linked list, which corresponds to a cluster.
                                if (list != null) { //This condition checks if the current cluster (list) is not null. It ensures that the loop doesn't attempt to iterate over a null cluster, which could result in a NullPointerException.
                                    for (Integer element : list) { //This enhanced for loop iterates over each element (element) in the current cluster (list). It iterates through all the integers stored in the linked list representing the cluster.
                                        StdOut.print(element + " "); //This statement prints each element followed by a space. It uses StdOut.print() to print without a newline character, allowing multiple elements to be printed on the same line.
                                    }
                                    StdOut.println(); //After printing all elements of the current cluster, this statement prints a newline character. It ensures that the elements of each cluster are printed on separate lines.

                                }
                            }
                        }
                        public LinkedList<Integer>[] getClusters() {
                            return clusters;
                        }
                        public LinkedList<Integer>[] getHashTable() {
                            return hashTable; // Return hashTable instead of clusters
                        }
                    


    
        


                        public static void main(String[] args) {
                            if ( args.length < 2 ) {
                                StdOut.println(
                                    "Execute: java -cp bin spiderman.Clusters <dimension INput file> <collider OUTput file>");
                                    return;
                            }
                            String inputFile=args[0];
                            String outPutFile = args[1];
                            StdIn.setFile(inputFile);
                            
                            int dimensions = StdIn.readInt();
                            int size = StdIn.readInt();
                            double capacity = StdIn.readDouble(); 
                            Clusters cluster12 = new Clusters(size, capacity);
                            
                            for( int x = 0; x<dimensions;x++) {
                                int dimensionNumber = StdIn.readInt();
                                int Events = StdIn.readInt();
                                int Weight = StdIn.readInt(); 
                                cluster12.insertDim(dimensionNumber,capacity,x);
                            }
                            cluster12.wrap(); 
                            StdOut.setFile(outPutFile);
                            cluster12.printClusters();
                            
                            
                        }
                        }

        