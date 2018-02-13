
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kmeans {

    //dataPoint[] data;
    private ArrayList<dataPoint> data = new ArrayList<dataPoint>();
    private int k = 2;									// number of clusters
    private int minObjects = 1;                                                             // min number of objects in each cluster before quitting
    //dataPoint[] cluster;			// the means for each cluster
    private ArrayList<dataPoint> cluster = new ArrayList<dataPoint>();

    // constructor initialize all the data points 
    public Kmeans(int k, int minObjects) {
        this.k = k;
        this.minObjects = minObjects;
        //data = new dataPoint[15];
        //cluster = new dataPoint[k];
        readFile();             // read the data from the file
        initMeans();            // set up the k clusters

    }
    private void readFile() {
        //File file = new File("C:\\Users\\pankaj\\Desktop\\test.txt");
        //File file = new File("test_data.txt");
        File file = new File("top_1000.txt");
        
        BufferedReader br;
        try {
            //String filePath = new File("").getAbsolutePath();
            //System.out.println("Working path is: " + filePath);
            //File f = new File("NEWFILE_FOR_TOM.txt");
            //f.createNewFile();
            br = new BufferedReader(new FileReader(file));

            String headings = br.readLine();
            
            String strLine;
            while ((strLine = br.readLine()) != null){
                
                System.out.println("Processing line: " + strLine);
                Scanner lineScanner = new Scanner(strLine); 
                dataPoint dp = new dataPoint(0);
                dp.setLabel(lineScanner.next());                        // read label as first item on line
                while(lineScanner.hasNextDouble())
                {
                    dp.addPoint(lineScanner.nextDouble());              // read each data point in tab-seperated file
                }
                data.add(dp);
                lineScanner.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Kmeans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.print("File not found error trying to open "+ "test_data.txt");
            Logger.getLogger(Kmeans.class.getName()).log(Level.SEVERE, null, ex);      
        }    
        
    } 

    // create a new data point from randomly combining parts of all the other data points
    public void setRandom_1(dataPoint dp ) {
        Random rn = new Random();
        dp.points.clear();          // erase current data points
        for (int i=0; i<data.get(0).getSize(); i++) {           
            int randIndex = rn.nextInt(data.size());        // pick a random other point to grad a index from
            //System.out.println("Updating point  " + i + " with random point "+ randIndex);
            dataPoint selected = data.get(randIndex);
            Double value = selected.getPoint(i);
            dp.addPoint(value);
        }     
    }
    
    // create a new data point from randomly picking another data point
    public void setRandom(dataPoint dp ) {
        Random rn = new Random();
        dp.points.clear();          // erase current data points
        int randIndex = rn.nextInt(data.size());        // pick a random other point to grad a index from
        dataPoint selected = data.get(randIndex);
        for (int i=0; i<data.get(0).getSize(); i++) {           
            //System.out.println("Updating point  " + i + " with random point "+ randIndex);
            Double value = selected.getPoint(i);
            dp.addPoint(value);
        }     
    }
    
    // create k means and set each to a random value
    public void initMeans() {
        for (int m = 0; m < k; m++) {
            dataPoint newdata = new dataPoint(m);
            newdata.clusterNum = m;
            newdata.setLabel("Cluster " + m);
            setRandom(newdata);
            cluster.add( newdata );

        }
    }

    // Assign each data point to the cluster represented by the nearest cluster mean
    // return true is some point is assigned to a new cluster
    public boolean findNearestMean() {
        double dist;
        int closest;
        boolean newCluster = false;
        //for (int d = 0; d < data.length; d++) {
        for (dataPoint dp: data) {
            // for each data point, assume it is closest to cluster[0] to start with. 
            dist = cluster.get(0).distanceTo(dp);			// record the distance to cluster[0]'s mean
            closest = 0;									// store the cluster number
            //for (int m = 1; m < cluster.length; m++) {
            for (dataPoint clust: cluster) {
                // for all the other cluster means starting with cluster[1], check if this data point is closer to it.
                double new_dist = clust.distanceTo(dp);
                if (dist > new_dist) {
                    dist = new_dist;
                    closest = clust.clusterNum;
                }
            }
            if (dp.clusterNum != closest) {
                // found a data point that must move.  Reset its cluster to the closest one
                dp.clusterNum = closest;				// store the current classification or nearest mean for this data point
                newCluster = true;					// remember that we moved at least on point to a new cluster
            }
        }
        return (newCluster);
    }

    // recalculate the cluster means for cluster m
    public void updateClusterCenter(dataPoint thisCluster) {
        // find the average x and y values for all data points in this cluster, that is those data points whose type is m
        // Here m is the cluster number of the cluster type.  All data points in this cluster will have a type equal to m
        //---------
        // this method must set cluster[m].x = average of all the data[i].x values for 
        //    the data points in this cluster, i.e. when data[i].type == m
        // this method must do the same for cluster[m].y
        // Warning: if there are not data points in cluster m, that is non of the data[i].type equal m
        //          then you can not calculate the averages and must ssign the cluster a new random location
        //          by calling:  cluster[m].setRandom();
        //System.out.println("Calculating new location for cluster " + thisCluster.clusterNum);
        //System.out.print("   This cluster is now at " );
        dataPoint sum = new dataPoint(0);
        sum.setSize( thisCluster.getSize() );
        int count = 0;
        //thisCluster.display();
        //for (int d = 0; d < data.length; d++) {
        for (dataPoint dp : data) {
            // check if this data point is in cluster m
            if (dp.clusterNum == thisCluster.clusterNum) {
                for (int i=0; i<dp.getSize(); i++) {                 
                   sum.setPoint(i, sum.getPoint(i)+dp.getPoint(i));
                }
                count ++;
            }
        }
        if (count>0) {
            for (int i=0; i<thisCluster.getSize(); i++) {
              thisCluster.setPoint(i, sum.getPoint(i)/count);
            }              
        }      
    }
    
    // count the number of objects in this cluster
    public int countCluster(dataPoint thisCluster) {
        int count = 0;			// used to store the count of the data points in the cluster
        for (dataPoint dp : data) {
            // if this data point is in cluster m, then count it
            if (dp.clusterNum == thisCluster.clusterNum) {
                count++;
            }
        } 
        return count;
    }
    
    // check for empty cluster with no nodes nearby
    public boolean checkEmptyCluster(dataPoint thisCluster) {
        int count = countCluster(thisCluster);
        if (count < minObjects) {
            // empty cluster, set to a random location
            //cluster.get(m).setRandom();
            setRandom(thisCluster);
            return true;
        }
        return false;
    }

    // print out a cluster and all the data points in it.
    // set detailLevel = 1 for just the number of elements in each cluster
    // set detailLevel = 2 for location of each cluster center
    // set detailLevel = 3 for list of points in each cluster
    public void displayClusters(int detailLevel) {
        System.out.println("============== Clusters ==============");
        //for (int m = 0; m < cluster.length; m++) {
        for (dataPoint clust: cluster) {
            
             if (detailLevel > 0) {
               System.out.print(clust.clusterNum + " contains " + countCluster(clust) + " -- ");             
            }           
            
            if (detailLevel > 1) {
               System.out.print("Cluster " + clust.clusterNum + " mean : ");
               clust.display();   
               for (dataPoint dp : data) {
                   if (dp.clusterNum == clust.clusterNum) {
                       System.out.print(dp.label + " , ");
                   }
               }   
               System.out.println();
            }
         
            if(detailLevel > 2) {
                for (dataPoint dp : data) {
                   if (dp.clusterNum == clust.clusterNum) {
                       System.out.print("data point " + dp.clusterNum + " : ");
                       dp.display();
                   }
                }               
            }

        }
        System.out.println();
    }

    // main routine that runs the k-means algorithm
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);    // needed to read input from the keyboard
        System.out.println("This program will automatically cluster the data points");
        System.out.println("How many clusters do you want to use?");
        int k = scnr.nextInt();
        System.out.println("Minimum number of objects in each cluster (commonly 1) ");
        int minObjects = scnr.nextInt();
        
        Kmeans kMeans = new Kmeans(k, minObjects);
        kMeans.displayClusters(3);					// display clusters
        
        // repeat the following until the data points stay in the same clusters
        // findNearestMean() will return true if it moves any data points to different clusters
        boolean foundEmpty = true;
        while (kMeans.findNearestMean() || foundEmpty) {
            // for each cluster do the following
            kMeans.displayClusters(1);					// display clusters
            foundEmpty = false;
            for (dataPoint clust: kMeans.cluster) {
            //for (int m = 0; m < kMeans.cluster.length; m++) {
                kMeans.updateClusterCenter(clust);		// move the cluster center to correspond to the data points in that cluster
                if (kMeans.checkEmptyCluster(clust)) {             // check for empty cluster
                    foundEmpty = true;
                    System.out.println("===== found an empty cluster ======");
                }
            }
            
        }
        
        kMeans.displayClusters(2);					// display clusters

    }

}
