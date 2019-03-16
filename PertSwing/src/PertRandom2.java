import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.*;

/**
 * Project Evaluation and Review Technique (PERT) static Object.
 *
 * <P>
 * Contains a static main method to read a file path argument supplied at
 * runtime. This file is parsed and a {@link myGraph} object is constructed with
 * the available vertices, edges. Given the different times [(o)ptimistic,
 * (p)essimistic and (m)ost likely] of completion, a normally distributed
 * finishing time is polled randomly from an adjusted Gaussian distribution,
 * with a mean and variance: .
 * <P>
 * mu = (o + 4m + p)/6
 * sigma2 = (p -o)^2/36
 * <P>
 * where o,m,p are optimistic, most likely and pessimistic finishing times.
 *
 * <P>
 * Note that {@link DecimalFormat} is used to format the percentage. Also {@link Random}
 *
 */
public class PertRandom2 {

    // Class Fields
    private static int iters = 0;
    private static int maxiters = 10000;
    private static DecimalFormat percent;

    /**
     * Main method for executing the file parsing and PERT solution using the
     * Graph class.
     *
     * @param args
     *            the file name of the file that holds the graph information
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        double expMean = 0;
        double expVar = 0;
        double sum = 0;
        double sq_sum = 0;
        SortedSet<Double> setCosts = new TreeSet<Double>();
        int bins [] = new int[30];
        Random r = new Random(10);
        myGraph g = new myGraph(21,60);


        //		 Find the Critical Path and print it out
        while (iters < maxiters ) {
            /**
             *  Create the Graph. The graph is created manually
             *  since when the detection of vertices and edges is applied
             *  to the while loop above the BufferedReader never blocks.
             *  The method would need to be synchronized which is not possible
             *  for this static class. Refactoring of this class could fix this
             *  but it was outside the scope of this assignment. Proof of
             *  concept is shown in Pert.java
             */
            g = new myGraph(21,60);
            // Don't print any verbose information
            g.quiet();

            // Count the lines to know a priori the number of the vertices
            FileReader file = new FileReader("C:\\Users\\barto\\Desktop\\PERT\\PertSwing\\src\\case101.dat");
            BufferedReader br = new BufferedReader(file);
            while(br.ready()) {
                String line = br.readLine();
                StringTokenizer tokens = new StringTokenizer(line);
                g.addVertex(tokens.nextToken());
            }

            br.close();


            FileReader file2 = new FileReader("C:\\Users\\barto\\Desktop\\PERT\\PertSwing\\src\\case101.dat");
            BufferedReader read = new BufferedReader(file2);
            while(read.ready()){
                String line = read.readLine();
                StringTokenizer tokens = new StringTokenizer(line);
                String name = tokens.nextToken();
                // Skip the optimistic value
                double opt = new Double(tokens.nextToken()).doubleValue();

                // Scrape the likely value
                double likely = new Double(tokens.nextToken()).doubleValue();

                // Skip the pessimistic value
                double pess = new Double(tokens.nextToken()).doubleValue();
                // Calculate the mean and variance of the distribution
                double costMean = (opt+4*likely+pess)/6;
                double costVar = Math.pow((pess-opt),2)/36;
                // Generate a normal value with the above factors for the Gaussian
                double cost = r.nextGaussian()*costVar+costMean;
                //System.out.println(cost);
                // Add all the dependencies as edges
                while(tokens.hasMoreTokens()){
                    g.addEdge(tokens.nextToken(), name, costMean);
                }
            }

            // Print the characteristics of the graph
//			g.print();

            // Find the critical path
            g.criticalPath();
//			System.out.println(g.criticalPathLength());
            sum += g.criticalPathLength();
            sq_sum += Math.pow(g.criticalPathLength(),2);
            // Keep a record of all costs for the final statistics
            setCosts.add(g.criticalPathLength());

            // Keep track of all iterations
            iters++;

            // Close the file-readers
            read.close();
            file.close();
        }
        /**
         * Calculate Mean and Variance of the critical path length in one run of the data.
         * Normally two passes of the data would be required to first calculate the mean and
         * then calculate the variance with the formula:
         *
         * var = Sum(Xi-Xmu)^2)/N  , for all i belongs to N
         *
         * However by decomposing the above formula the equivalent:
         *
         * var = (Sum(Xi))^2/N - (Sum/N)^2  , for all i belongs to N
         */
        expMean = sum/iters;
        expVar =  sq_sum/iters - Math.pow(expMean, 2);

        System.out.println("Experiment's mean value for the critical length paths: "+expMean);
        System.out.println("Experiment's variance value for the critical length paths: "+expVar);
        System.out.println("Critical Path: "+g.verticesCriticalPath());
        int index = 0;

    }
}
