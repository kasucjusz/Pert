import java.util.*;


public class myGraph {

    /* Here are the class variables, they store information about the Graph */
    private int maxVertices, maxEdges; // max dimensions of the Graph
    private int nVertices, nEdges; // current dimensions of the Graph
    private String[] vertices;
    private double edgeCost[];
    private int edgeFrom[];
    private int edgeTo[];
    Random r = new Random();
    private String startNode;
    private String endNode;
    double INF = Double.MAX_VALUE;

    // Fields necessary for Dijkstra's
    private final Set<String> settledV = new HashSet<String>();
    private final PriorityQueue<String> unSettledV;
    private final Map<String, Double> d = new HashMap<String, Double>();
    private final Map<String, String> pred = new HashMap<String, String>();


    private final Comparator<String> minDistanceComparator = new Comparator<String>() {

        @Override
        public int compare(String from, String to) {
            double result = getMinDistance(from) - getMinDistance(to);
            return (int) ((result == 0) ? from.compareTo(to) : result);
        }
    };

    /*
     * These need to be set by the criticalPath method. The methods
     * criticalPathLength and verticesCriticalPath depend on these being set
     * correctly
     */
    private int idxNextVertex[];
    private double lengthCriticalPath = 0.0;

    private boolean print; // to indicate if the class should print anything

    /* stuff for the critical path method */


    myGraph(int maxVertices, int maxEdges) {
        // remember passed parameters
        this.maxVertices = maxVertices;
        this.maxEdges = maxEdges;

        // set edge and vertex counters to 0
        nVertices = 0;
        nEdges = 0;

        // initialise all arrays now that we know the dimension
        vertices = new String[maxVertices];
        edgeCost = new double[maxEdges];
        edgeFrom = new int[maxEdges];
        edgeTo = new int[maxEdges];

        // do printing by default
        print = true;
        unSettledV = new PriorityQueue<String>(maxVertices,
                minDistanceComparator);

    }

    /** Switches off printing */
    public void quiet() {
        print = false;
    }


    public void addVertex(String vert) {
        // check that there is still space in the arrays
        if (nVertices >= maxVertices) {
            throw new IndexOutOfBoundsException("No space for more vertices");
        }

        // check whether vertex is known already
        boolean found = false;
        for (int i = 0; i < nVertices; i++) {
            if (vertices[i].equals(vert))
                found = true;
        }

        if (found) {
            System.out.println("GRAPH: vertex " + vert + " is already known");
        } else {
            // if not then put vertex in array and increase counter
            if (print)
                System.out.println("GRAPH: Add Vertex: " + vert);
            vertices[nVertices] = vert;
            nVertices++;
        }
    }

    public void addEdge(String from, String to, double cost) {
        int i1, i2;

        // find the indices of the start and end vertex in the vertex-array
        i1 = findVertex(from);
        i2 = findVertex(to);

        // check if there is space for more edges
        if (nEdges >= maxEdges) {
            throw new ArrayIndexOutOfBoundsException("No space for more edges");
        }

        if (print)
            System.out.println("GRAPH: Add Edge: " + from + " - " + to + " : "
                    + cost);

        // put edge information in arrays and increase edge counter
        edgeCost[nEdges] = cost;
        edgeFrom[nEdges] = i1;
        edgeTo[nEdges] = i2;
        nEdges++;
    }

    public void criticalPath() {

        // allocates the idxNextVertex array
        idxNextVertex = new int[nVertices];

        if (print)
            System.out.println("GRAPH: Find critical path");

        // Determine the start and end of the graph tree
        this.getStart();
        this.getEnd();

        setMinDistance(startNode, 0); // Begin from the start node and set its
        // distance to 0


        runBellmanFord(); 				// The dynamic programming algorithm finally used
    }

    private void runBellmanFord() {
        // the 'distance' array contains the distances from the main source to
        // all other nodes
        double[] distance = new double[nVertices];
        // at the start - all distances are initiated to infinity
        Arrays.fill(distance, INF);
        // the distance from the end vertex to itself is 0
        distance[findVertex(startNode)] = 0;
        // set the start nodes' predecessor
        pred.put(startNode, null);
        // Relaxing all edges for all vertices
        for (int i = 0; i < nVertices; ++i)
            // relax every edge in 'edges'
            for (int j = 0; j < nEdges; ++j) {
                if (distance[edgeFrom[j]] == INF)
                    continue;
                double newDistance = distance[edgeFrom[j]] - edgeCost[j];
                if (newDistance < distance[edgeTo[j]]) {
                    distance[edgeTo[j]] = newDistance;
                    pred.put(vertices[edgeTo[j]], vertices[edgeFrom[j]]);
                }
            }
        // Check if there are any negative cycles. Benefit of the BF algorithm
        // Break if there are cycles and don't find a critical path for the
        // graph.
        for (int i = 0; i < nEdges; ++i)
            if (distance[edgeFrom[i]] != INF
                    && distance[edgeTo[i]] > distance[edgeFrom[i]]
                    + edgeCost[i]) {
                if (print) System.out.println("Cycles detected!");
                return;
            }

        for (int i = 0; i < distance.length; ++i)
            if (distance[i] == INF)
                System.out.println("There's no path between " + startNode
                        + " and " + endNode);
        // this loop outputs the distances from the main source node to all
        // other nodes of the graph
        // for (int i = 0; i < distance.length; ++i)
        // if (distance[i] > lengthCriticalPath && distance[i] != INF)
        // lengthCriticalPath = distance[i];

        // Populate the path in the array that prints the critical path in the end
        populatePath(distance);
    }


    private void populatePath(double[] distance) {
        idxNextVertex[findVertex(endNode)] = -1; // Condition for the print loop
        // to finish on the last
        // node.
        String vertex = endNode;
        String p = "";
        while (true) {
            for (Map.Entry<String, String> previous : pred.entrySet()) {
                if (previous.getKey() == vertex) {
                    double maxDist = INF;
                    if (previous.getValue() != null) {
                        if (vertices[findVertex(previous.getKey())] == vertex
                                && distance[findVertex(previous.getValue())] < maxDist) {
                            maxDist = distance[findVertex(previous.getKey())];
                            p = vertices[findVertex(previous.getValue())];
                        }
                    } else {
                        // The startNode has been reached
                        p = null;
                        break;
                    }
                }
                if (print)
                    System.out.println("finished with predecessors");
            }
            int previous = 0;
            if (p != null) {
                // Get the weight of the previous edge
                double edgeC = 0;
                for (int i = 0; i < maxEdges; i++) {
                    if (vertices[edgeFrom[i]] == p
                            && vertices[edgeTo[i]] == vertex)
                        edgeC = edgeCost[i];
                }
                lengthCriticalPath += edgeC;
                previous = findVertex(p);
                idxNextVertex[previous] = findVertex(vertex);
                vertex = vertices[previous];
            } else
                break;
        }
        if (print)
            System.out.println("finished populating");
    }

    public void setMinDistance(String vertex, double distance) {
        unSettledV.remove(vertex); // so that we don't have duplicates in the
        // queue when updated with a newer distance
        d.put(vertex, distance);
        unSettledV.add(vertex); // Re-balance the queue with the new distance
    }

    private double getMinDistance(String from) {
        Double dist = d.get(from);
        return (dist == null) ? Integer.MAX_VALUE : dist;
    }



    public String verticesCriticalPath() {
        // Initialise the list with the name of the first vertex
        String list = vertices[0];
        int currentIdx = 0;

        // while there is a next vertex on the critical path
        while (idxNextVertex[currentIdx] >= 0) {
            currentIdx = idxNextVertex[currentIdx];
            // add the name of the current vertex to the list
            list += " - " + vertices[currentIdx];
        }
        return list;
    }


    public double criticalPathLength() {

        return lengthCriticalPath;
    }



    private void getEnd() {
        for (int i = 0; i < vertices.length; i++) {
            int from = 0;
            if (vertices[i] != null)
                from = findVertex(vertices[i]);
            for (int j = 0; j < edgeFrom.length; j++) {
                if (edgeFrom[from] != from)
                    endNode = vertices[i];
            }
        }
        if (print)
            System.out.println(endNode);
    }


    private void getStart() {
        startNode = vertices[0];
        if (print)
            System.out.println(startNode);
    }

    private int findVertex(String vertex) {
        int i1 = -1;
        boolean found = false;

        // scan through all vertices already known
        for (int i = 0; i < nVertices; i++) {
            if (vertices[i].equals(vertex)) {
                // if found the correct one => remember and stop the loop
                found = true;
                i1 = i;
                break;
            }
        }

        // if not found throw an exception
        if (!found) {
            throw new IllegalArgumentException("Vertex " + vertex
                    + " not in graph");
        }

        // else return the index
        return i1;
    }



}
