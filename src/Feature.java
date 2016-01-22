import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Feature {
	private static final double INITIAL_DIST = 9999;
	private List<VarNameFeature> variantNameList = new ArrayList<VarNameFeature>();
	private String wordBase;
	
	//Stats
	private double standardDev;
	private double mean;
	private double variance;
	private double median;
	
	//To create the list of features.
	static class VarNameFeature{
		final String variantName;
		double distance;
		final int wordBasePos;
		
		VarNameFeature(String variantName, double distance){
			this.variantName = variantName;
			this.distance = distance;
			this.wordBasePos = 0;
		}
		
		VarNameFeature(String variantName, double distance, int wordBasePos) {
			this.variantName = variantName;
			this.distance = distance;
			this.wordBasePos = wordBasePos;
		}
	}
	
	//It sets both the name and the distance to the base word.
	public void setFeature(String variantName, double distance, int pos) {
		VarNameFeature feature = new VarNameFeature(variantName, distance, pos);
		this.variantNameList.add(feature);
	}
	
	//Creates a new feature with distance = 0 and add to the list.
	public void setVariantName(String variantName) {
		VarNameFeature feature = new VarNameFeature(variantName, INITIAL_DIST);
		this.variantNameList.add(feature);
	}
	
	//Updates the distance of the passed variant name to the value passed
	public void setDistance(String variantName, double newDistance) {
		VarNameFeature newFeature  = new VarNameFeature(variantName, newDistance);
		
		//Adds the new distance name when the variant already exists. 
		if(this.variantNameList.remove(getFeature(variantName))){
			this.variantNameList.add(newFeature);
		}
		else{
			this.variantNameList.add(newFeature);
		}
	}
	
	//It returns a specific element from the list
	public VarNameFeature getFeature(String varName){
		VarNameFeature feature = null;
		
		for (VarNameFeature element : this.variantNameList) {
			if(element.variantName.equals(varName)){
				feature = element;
				break;
			}
		}
		
		return feature;
	}
	
	public int getPosOf(String varName) {
		for (int i = 0 ; i< this.variantNameList.size(); i++) {
			if(this.variantNameList.get(i).variantName.equals(varName)){
				return i;
			}
		}
		return -1;
	}
	
	public VarNameFeature getAtPos(int pos) {
		return variantNameList.get(pos);
	}
	
	//Return the list of features with all variant names.
	public List<VarNameFeature> getAllFeatures(){
		return this.variantNameList;
	}
	
	public String getWordBase() {
		return wordBase;
	}
	
	public void setWordBase(String wordBase) {
		this.wordBase = wordBase;
	}

	public void updateDistance(Feature distances) {
		int size = this.variantNameList.size();
		
		for(int i = size-1; i >= 0; i--){
			if(distances.getAtPos(i).distance != INITIAL_DIST){
				String varName = distances.getAtPos(i).variantName;
				double newDist = distances.getAtPos(i).distance;
				
				int pos = getPosOf(varName);
				
				double oldDist = this.variantNameList.get(pos).distance;
				if(oldDist == INITIAL_DIST){
					this.variantNameList.get(pos).distance = newDist; 
				}else{
					this.variantNameList.get(pos).distance = (newDist+oldDist)/2;
				}
			}
			else break;
		}
	}
	
	public void setStats(double sd, double mean, double variance, double median) {
		standardDev = sd;
		this.mean = mean;
		this.median = median;
		this.variance = variance;
	}
	
	public double[] getStats() {
		/*
		 * Returns an array with the stats.
		 * Position
		 * 	0: Standard deviation
		 * 	1: Mean
		 * 	2: Variance
		 * 	3: Median 
		*/
		double[] stats = {0,0,0,0};
		
		stats[0] = standardDev;
		stats[1] = mean;
		stats[2] = variance;
		stats[3] = median;
		
		return stats;
	}
	
	@Override
	public String toString() {	
		String tmp = "";
		for (VarNameFeature feature : variantNameList) {
			tmp = tmp+"D("+feature.variantName+","+wordBase+") = "+feature.distance+"\n";
		}
		tmp = tmp+"SD: "+standardDev+"\nMean: "+mean+"\nVar: "+variance+"\nMedian: "+ median;
		return "[Word Base]: " + wordBase +"\n" + "[Variant names]:\n" + tmp + "\n";
	}
}
