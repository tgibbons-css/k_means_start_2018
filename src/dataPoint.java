
import java.util.ArrayList;


public class dataPoint {

	 final int num_parameters=2;		// number of parameters or characteristics for each data point
         public ArrayList<Double> points = new ArrayList<Double>();
	 //public double[] x = new double[num_parameters];	// the parameters for this data point
	 //public double x;				// holds the height
	 //public double y;				// holds the circumf.
	 public int clusterNum;				// the classification or type of this data point
	 public String label;                           // label for this data point

	 public dataPoint(int newIndexNum){
		 clusterNum = newIndexNum;
	 }
         
         public dataPoint(int newIndexNum, int numPoints){
		 clusterNum = newIndexNum;

	 }
         
         public void addPoint(Double newPoint) {
             points.add(newPoint);
         }
         
         public int getSize() {
             return points.size();
         }
         
         public void setSize(int newSize) {
            for (int i=0; i<newSize; i++) {
                addPoint(0.0);
            }
         }
         
         public Double getPoint(int index) {
             return points.get(index);
         }
         
        public void setPoint(int index, Double value) {
             points.set(index, value);
         }
         
         
         public void setLabel( String newLabel) {
             label = newLabel;
         }
         
        public String getLabel( ) {
             return label;
         }
        
         public void setClusterNum(int newClusterNum) {
             clusterNum = newClusterNum;
         }      
         
        public int getClusterNum( ) {
             return clusterNum;
         }
	   
	 public double distanceTo(dataPoint p2) {
		 double dist=0;
		 //dist = Math.sqrt( Math.pow(x - p2.x,2) + Math.pow(y - p2.y,2));
		 
		 for (int i=0; i<points.size(); i++) {
			 dist += Math.pow(points.get(i) - p2.points.get(i),2);
		 }
		 dist = Math.sqrt(dist);
		 
		 return dist;
	 }
	 
	 public void display() {
            //System.out.println(label+" at x = "+x+" and y = "+y);
            System.out.print(label +" - " + clusterNum + " at ");
            for (Double point : points) {
                System.out.print(String.format( "%.1f, ", point ));
            }
            System.out.println();
         }
	
	 
}


