import java.io.*;
import java.text.DecimalFormat;
import java.util.*;


public class PertRandom2 {

    // Class Fields
    private static int iters = 0;
    private static int maxiters = 10000;


    public static void main(String[] args) throws IOException {
        double expMean = 0;
        double expVar = 0;
        double sum = 0;
        double sq_sum = 0;
        SortedSet<Double> setCosts = new TreeSet<Double>();
        int bins [] = new int[30];
        Random r = new Random(10);
        myGraph g = new myGraph(21,60);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter a number o max weeks: ");
        float numberOfWeeks = reader.nextFloat(); // Scans the next token of the input as an int.
        reader.close();



//////////////////////////////////////////////////////////////////////////////////////////////////////
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
            FileReader file = new FileReader("C:\\Users\\barto\\Desktop\\PERT\\PertSwing\\src\\case103.dat");
            BufferedReader br = new BufferedReader(file);
            while(br.ready()) {
                String line = br.readLine();
                StringTokenizer tokens = new StringTokenizer(line);
                g.addVertex(tokens.nextToken());
            }

            br.close();


            FileReader file2 = new FileReader("C:\\Users\\barto\\Desktop\\PERT\\PertSwing\\src\\case103.dat");
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

        expMean = sum/iters;
        //expVar =  sq_sum/iters - Math.pow(expMean, 2);

        double globalTMP=0;
        String wariancjaKrytyczna=g.verticesCriticalPath();





        FileReader file3 = new FileReader("C:\\Users\\barto\\Desktop\\PERT\\PertSwing\\src\\case103.dat");
        BufferedReader read = new BufferedReader(file3);
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
            double costVar = Math.pow((pess-opt),2)/36;


                if(wariancjaKrytyczna.contains(name))
                {
                   // System.out.println(costVar);
                    globalTMP+=costVar;
                }









            }


        double wynikRadek=(numberOfWeeks-expMean)/(Math.sqrt(globalTMP));

        System.out.println("Experiment's mean value for the critical length paths: "+expMean);
        System.out.println("Sum of variances is equal to:  " + globalTMP);
        System.out.println("X is equal to: "+ wynikRadek);
        System.out.println("Critical Path: "+g.verticesCriticalPath());



    }






    }

