
public class Attributes {
	private String word = null;
	private String keyword = null;
	private int distance;
	private String related = null;
	private String lowercase = null;
	private String label = null;
	private String stopword = null;
	private String distanceOutput = null;
	
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}
	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	/**
	 * @return the related
	 */
	public String getRelated() {
		return related;
	}
	/**
	 * @param related the related to set
	 */
	public void setRelated(String related) {
		this.related = related;
	}
	/**
	 * @return the lowercase
	 */
	public String getLowercase() {
		return lowercase;
	}
	/**
	 * @param lowercase the lowercase to set
	 */
	public void setLowercase(String lowercase) {
		this.lowercase = lowercase;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the stopword
	 */
	public String getStopword() {
		return stopword;
	}
	/**
	 * @param stopword the stopword to set
	 */
	public void setStopword(String stopword) {
		this.stopword = stopword;
	}
	/**
	 * @return the distanceOutput
	 */
	public String getDistanceOutput() {
		return distanceOutput;
	}
	/**
	 * @param distanceOutput the distanceOutput to set
	 */
	public void setDistanceOutput(String distanceOutput) {
		this.distanceOutput = distanceOutput;
	}
	
	@Override
    public Attributes clone() {
        Attributes attr = new Attributes();
        
        attr.distance = this.distance;
        attr.distanceOutput = this.distanceOutput;
        attr.keyword = this.keyword;
        attr.label = this.label;
        attr.lowercase = this.lowercase;
        attr.related = this.related;
        attr.stopword = this.stopword;
        attr.word = this.word;
        
        return attr;
    }

	@Override
	public String toString() {
		
		return word + (keyword == null? " ": " "+keyword+" ") + "distance="+distance + (related == null? " ": " "+related+" ")+ lowercase +
				(stopword == null? " ": " "+stopword+" ")+label;
	}
}
