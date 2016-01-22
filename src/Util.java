import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import java.util.regex.*;

import org.w3c.dom.Attr;


public class Util<VarNameFeature> {
	
	public List<PubmedDocument> simpleMatcher(List<PubmedDocument> doc, String pattern) {
	/*
	 * This method compare the pattern with the documents. This is not a long common subsequence. 
	 * It just checks if the documents contains the given pattern.
	 * 
	 * Variations of the pattern might include one of these characters before or after it.
	 * But we can only allow 1 character of difference(distance).
	 * ( ) , - / \ " " ' ' . ? < > \r
	 * { } [ ] 
	 * Cases like Brachyury(T) are hard to determine, but this regex work for those cases.
	 * ("^[^0-9a-zA-Z-_]"+pattern+"[^0-9a-zA-Z-_]$");
	 * 
	 * It fails when the gene name appears followed by cell or cells.
	 * But if one uses the tmsk tool, this is solved, for cell and cells are considered a stopword.
	 * 
	 * This method add to a stack only ID's of files that have high likelihood that the string refers to
	 * a Gene.
	*/
		
		Stack<PubmedDocument> result = new Stack<PubmedDocument>();
		String contentAbstract, contentTitle;
		
		for (PubmedDocument cont : doc){
			boolean isGene = false;
			contentTitle = cont.getTitle();
			contentAbstract = cont.getAbstract_text();
			
			if(contentTitle != null){
				isGene = MatcherAux(pattern, contentTitle);
			}
			if(contentAbstract != null){
				isGene = MatcherAux(pattern, contentAbstract);
			}
			
			if(isGene){
				/*
				 * Saving the info of the docs that have the pattern that we look for.
				 * */
				PubmedDocument currentDoc = new PubmedDocument();
				currentDoc.setId(cont.getId());
				currentDoc.setJournal(cont.getJournal());
				currentDoc.setAbstract_text(cont.getAbstract_text());
				currentDoc.setTitle(cont.getTitle());
				
				result.push(currentDoc);
			}
		}
		
		return result;
	}
	
	public boolean MatcherAux(String pattern, String content) {
		boolean result = false;
		
		//This first pattern is to extract the candidates of real Gene names from 'content'
		Pattern patternCandidates = Pattern.compile(pattern);
		
		/*
		 * This is to verify if the 'candidate' is an actual Gene name.
		 * This new pattern accepts (), [], {}, <>, spaces, tabulation, etc, surrounding the candidate.
		*/		
		Pattern patternVerification = Pattern.compile("^[^0-9a-zA-Z-_^&+#@~']"+pattern+"[^0-9a-zA-Z-_^&+#@~']$");
		
		//Matching possible candidates.
		Matcher matcherCandidadtes = patternCandidates.matcher(content);
		
		while(matcherCandidadtes.find()){
			int start = matcherCandidadtes.start(0);
			int end = matcherCandidadtes.end(0);
			
			//This line checks for the surroundings of the candidate with one character to each side.
			Matcher matcherVerification = null;
			if(start == 0 && end < content.length()){
				String candidate = content.substring(start, end+1);
				matcherVerification = patternVerification.matcher(candidate);
			}else if(start > 0 && end == content.length()){
				String candidate = content.substring(start-1, end);
				matcherVerification = patternVerification.matcher(candidate);
			}else if(start == 0 && end == content.length()){
				result = true;
			}else{
				String candidate = content.substring(start-1, end+1);
				matcherVerification = patternVerification.matcher(candidate);
			}
	
			if(matcherVerification.find()) result = true;
		}
		
		return result;
	}
	
	public void saveFile(String content, String path, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(path+fileName, "UTF-8");//Overwrites in case the file already exists.
		
		writer.println(content);

		writer.close();
	}
	
	public String concat(int mode, List<PubmedDocument> files){
		/*
		 * When calling this functions the user can choose what they want to concatenate.
		 * Table of available number and its meaning.
		 * 0 - To concatenate all the Abstracts of the list into a string.
		 * 1 - To concatenate all the Title of the list into a string.
		 * 2 - To concatenate all the Journal names of the list into a string.
		 * 3 - To concatenate all the ID's of the list into a string.
		*/
		String concatenated = "";
		
		switch (mode){
			case 0://Abstracts
				
				//This loop concatenates the Abstracts into a single string to save it into a file.
				for (PubmedDocument doc : files) {
			    	concatenated = concatenated+"\n"+doc.getAbstract_text();
				}
				break;
			case 1://Titles
				
				//This loop concatenates the Titles into a single string to save it into a file.
				for (PubmedDocument doc : files) {
			    	concatenated = concatenated+"\n"+doc.getTitle();
				}

				break;
			case 2://Journal names
				
				//This loop concatenates the Journal Names into a single string to save it into a file.
				for (PubmedDocument doc : files) {
			    	concatenated = concatenated+"\n"+doc.getJournal();
				}

				break;
			case 3://ID's
				
				//This loop concatenates the ID's into a single string to save it into a file.
				for (PubmedDocument doc : files) {
			    	concatenated = concatenated+"\n"+doc.getId();
				}

				break;
			default:
				System.out.println("Not available field for chosen option.\n" +
						"Available options: 0 = Abstracts, 1 = Titles, 2 = Journal Names and 3 = ID's");
				break;
			
		}
		
		return concatenated;
	}
	
	static class Rule{
	    final String name;
	    final Pattern pattern;

	    Rule(String name, String regex){
	      this.name = name;
	      pattern = Pattern.compile(regex);
	    }
	}

	public ArrayList<String> tokenize(String source){
		List<Rule> rules = new ArrayList<Rule>();
		rules.add(new Rule("word", "[A-Za-z0-9_%+]+"));
		//rules.add(new Rule("delim", "[^\\n\\t\\r,.;:!?&()<>+\"\']" ));
		rules.add(new Rule("nonEngWord", "[\\p{IsLatin}]+"));
		
	    ArrayList<String> tokens = new ArrayList<String>();
	    
	    int pos = 0; 
	    final int end = source.length();
	    
	    Matcher m = Pattern.compile("dummy").matcher(source);
	    m.useTransparentBounds(true).useAnchoringBounds(false);
	    
	    while (pos < end)
	    {
	      m.region(pos, end);
	      for (Rule r : rules)
	      {
	        if (m.usePattern(r.pattern).lookingAt())
	        {
	          tokens.add((String) source.subSequence(m.start(), m.end()));
	          pos = m.end();
	          break;
	        }
	      }
	      pos++;  // bump-along, in case no rule matched
	    }
	    
	    return tokens;
	}
	
	public Feature calcDistance(Feature feature, String document) {
		/*
		 * This method calculates the distance between the variant name and the word of interest.
		 * It receives 'feature' that contains the word of interest and the words of the dictionary generated by tmsk tool.
		 * The second parameter is 'document', which is to be tokenized and measured. 
		 *  
		 * Firstly it finds the position of each variant name and word of interest. There is one list for the word of interest 
		 * and another list for the variant names. These lists record the position at which the words were found.
		 * 
		 * This method return the features and their distances to the word base. For each time a word in the list of features appears
		 * in the document, the word and the distance of it to the word base is added to 'calculatedDist'.
		*/
		
		//Document tokenized
		ArrayList<String> tokens = new ArrayList<String>();
		tokens = tokenize(document);
		
		//To store the position of the word of interest
		List<Integer> position = new ArrayList<Integer>();
		
		//To store the words found in the document that are present in the list of features
		String wordBase = feature.getWordBase();
		Feature tmp = new Feature();
		
		for (int i = 0; i < tokens.size(); i++) {
			String word = tokens.get(i);
			
			//When the document has the word of interest, the position of it will be stored.
			if(wordBase.equals(word)){
				position.add(i);
			}
			//When the document has one of the features, the position of it is stored.
			else if(feature.getFeature(word)!= null){
				tmp.setFeature(word, i, i);
			}
		}
		
		//Calculates the distance of each feature found in the document to each word of interest.
		List<Feature.VarNameFeature> featuresFound = new ArrayList<Feature.VarNameFeature>();
		featuresFound = tmp.getAllFeatures();
		
		//Final variable that contains the features found and their distances.
		Feature calculatedDist = new Feature();
		calculatedDist.setWordBase(wordBase);
		
		for (Integer pos : position) {
			for (int i = 0; i < featuresFound.size(); i++) {
				String featFound = featuresFound.get(i).variantName;
				double dist = featuresFound.get(i).distance - (double)pos;
				calculatedDist.setFeature(featFound, dist, pos);
			}
		}
		
		return calculatedDist;
	}
	
	public Feature getDistances(String gene, BufferedReader dictionary, List<PubmedDocument> documents, String path) {
		/*
		 * This method makes use of the method calcDistances.
		 * Right now, it only handles the abstracts.
		 * 
		 * This method receives the dictionary generated by tmsk tool, and all documents that contain the word of interest.
		 * It also receives the boolean saveFile, that will save the files: Positive distances, Negatives distances and Final distances.
		 * More info on the files follows in the comments of the specific file to be saved.
		 * The method returns a single Feature containing all words from 'dictionary' that appeared in 'documents' with its distances.
		 * 
		 * The distance is the minimum absolute distance of a word in the dictionary to the word of interest.
		*/
		
		Feature result = new Feature();
		result.setWordBase(gene);
		Feature posFeature = new Feature();
		Feature negFeature = new Feature();
		posFeature.setWordBase(gene);
		negFeature.setWordBase(gene);
		
		//Temporary Feature to store the lowest values of each word found.
  		Feature tmpPosFeature = new Feature();
		Feature tmpNegFeature = new Feature();
		
		//Each word (line of the file).
		String line;
	    try {
			while((line = dictionary.readLine()) != null)	//It only reads one word in the dictionary per time.
			{
				/*The first step is to determine the distance of the surroundings of the word of interest.
				 * At first we don't know which distance to keep, so we'll calculate words before(negative) and after(positive) the WoI.
				*/
			    posFeature.setVariantName(line);
			    negFeature.setVariantName(line);
			    
			    /*
			     * For each word in the dictionary we'll keep track of the smallest distance to the WoI.
			    */
			    tmpNegFeature.setVariantName(line);
			    tmpPosFeature.setVariantName(line);
			    
			    //For the final result
			    result.setVariantName(line);
			    
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    Feature distances;
	    for (PubmedDocument pubmedDocument : documents) {
	    	
	    	//Checking if the doc has a non null abstract
	    	if(pubmedDocument.getAbstract_text() != null){
	    		
	    		//Checking if the WoI is present in the abstract.
	    		if(pubmedDocument.getAbstract_text().contains(gene)){
	    			
	    			//Calls the auxiliary function that calculates the distances of all words in the dic to one file.
		    		distances =  calcDistance(posFeature, pubmedDocument.getAbstract_text());	//It could've been pos or negFeature in there.
		    		
			    	for (int i = 0; i < distances.getAllFeatures().size(); i++) {
			    		double dist = distances.getAtPos(i).distance;	//All initial distances are 9999.
			    		String varName = distances.getAtPos(i).variantName;
			    		
						if (dist < tmpPosFeature.getFeature(varName).distance && dist > 0){
							tmpPosFeature.setDistance(varName, dist);
						}
						else if(Math.abs(dist) < Math.abs(tmpNegFeature.getFeature(varName).distance) && dist < 0){
							tmpNegFeature.setDistance(varName, dist);
						}		
					}
				    posFeature.updateDistance(tmpPosFeature);
				    negFeature.updateDistance(tmpNegFeature);
				    
				    //Resetting all distances to 9999.
				    int size = tmpPosFeature.getAllFeatures().size();
				    size--;
			    	while(size >= 0)
				    {
				        tmpNegFeature.getAtPos(size).distance = 9999;
				        tmpPosFeature.getAtPos(size).distance = 9999;
				        size--;
				    }
	    		}
	    	}
		}
	    
	    int size = result.getAllFeatures().size();
	    size--;
    	while(size >= 0)
	    {
	        result.getAtPos(size).distance = 9999;
	        size--;
	    }
	    
	    for(int i = 0; i < posFeature.getAllFeatures().size(); i++){
	    	double closer;
	    	if(posFeature.getAtPos(i).distance < Math.abs(negFeature.getAtPos(i).distance)){
	    		closer = posFeature.getAtPos(i).distance;
	    		result.setDistance(posFeature.getAtPos(i).variantName, closer);
	    	}
	    	else{
	    		closer = negFeature.getAtPos(i).distance;
	    		result.setDistance(negFeature.getAtPos(i).variantName, closer);
	    	}
	    }
	    
	    //Saving
	    if(path != null){
	    	try {
				saveFile(posFeature.toString(), path, "PosDistances.txt");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    	try {
				saveFile(negFeature.toString(), path, "NegDistances.txt");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    	try {
				saveFile(tmpPosFeature.toString(), path, "FinalDistances.txt");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    }
	    
		return result;
	}
	
	public Feature calcStats(String path, Feature distances, int distForSD) {
		/*
		 * It receives the distances of the words in the dic, the parameter that specifies when we want to save words within 1, or 2, or 3 SD.
		*/
		int length = distances.getAllFeatures().size();
	    double dist[] = new double[length];
	    for (int i = 0; i < length; i++) {
	    	double storedDist = distances.getAtPos(i).distance; 
	    	
	    	//Making sure we don't add those words that didn't get changed.
	    	if(storedDist != 9999)
	    		dist[i] = storedDist;
		}
	    
	    Statistics stats = new Statistics(dist);
	    double standardDev = stats.getStdDev();
	    
	    //Removing words that have distance greater than 1 or 2 or 3 sd.
	    Feature finalList = new Feature();
	    finalList.setWordBase(distances.getWordBase());		//Setting the WoI.
	    
	    for (int i = 0; i < length; i++) {
	    	double storedDist = distances.getAtPos(i).distance; 
	    	
	    	//Adding to the final list
	    	if(Math.abs(storedDist) <= distForSD*standardDev){
	    		finalList.setFeature(distances.getAtPos(i).variantName, storedDist, 0);
	    	}
		}
	    
	    //Setting the values in the variable of return.
	    finalList.setStats(standardDev, stats.getMean(), stats.getVariance(), stats.median());
	    
	    //If the path is specified, a file will be created.
	    if(path != null){
		    try {
				saveFile(finalList.toString(), path, "FinalList.txt");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    }
	    
	    return finalList;
	}
	
	public List<List<Attributes>> stringToCollection(List<String> sentences, String gene) {
		/*
		 * The return is a list with a list of sentences that contain the String gene. Each position of
		 * the most external list is a sentence. Each position of the most internal list is a node Attribute
		 * in the sentence. The class Attributes.java specifies each field in the node.
		 * 
		 * This method converts the List<String> input into a List<List<Attributes>>,
		 * which was specified above.
		 * 
		 * The List<String> input is the sentences of the file.
		*/

		List<Attributes> attr;								//To store each single word within one sentence.
		ArrayList<String> tokens = new ArrayList<>();		//To store the tokens.
		List<List<Attributes>> collection = new ArrayList<List<Attributes>>();	//To store all sentences after they are converted to collection.
		for (String sentence : sentences) {
			attr = new ArrayList<Attributes>();
			if(MatcherAux(gene, sentence)){					//Verifies if the sentence has the word of interest.
				tokens = tokenize(sentence);
				for (int i=0; i<tokens.size(); i++) {
					Attributes tmp = new Attributes();
					
					tmp.setWord(tokens.get(i));
					attr.add(tmp);
				}
				collection.add(attr);
			}
		}

		return collection;
	}
	
	public List<List<Attributes>> calcRelDist(List<List<Attributes>> collection, String gene) {
		/*
		 * This method calculates the relative distance between the String gene and each other word in the sentence.
		 * Note that the String Gene can appear more than once in the text, so we need to deal with those situations.
		 * 
		 * The method receives the return of calcAbsDist.
		 * 
		 * The return is a newCollection that contains the same info as returned in calcAbsDist, the difference lies on the fact 
		 * that for each String gene in the sentence, a new collection is created. As a result the size of List<List<Attibutes>>
		 * is likely to be greater than calcAbsDist, for the String gene can appear more than once in the same sentence. 
		*/
		List<List<Attributes>> newCollection = new ArrayList<List<Attributes>>();
		List<Integer> position;
		List<Attributes> tmp;		//Variable that will store the sentence being calculated at the moment.
		for (List<Attributes> list : collection) {
			position = getPositions(list, gene);
			
			for (int j = 0; j < position.size(); j++) {			//For each String gene found in the sentence.
				tmp = cloneList(list);							//The most recent sentence is stored here.
				
				for (int k = 0; k < tmp.size(); k++)			//For each word in the sentence.
					tmp.get(k).setDistance(k - position.get(j));//Updating the distance. Relative distance.

				newCollection.add(tmp);							//newCollection contains all occurrences of String gene in all sentences.
																//for each occurrence.
			}
		}
		
		return newCollection;
	}
	
	public List<List<Attributes>> markWords(List<List<Attributes>> collection, String stopwords, String dictionary) {
		/*
		 * The method receives the return of calcRelDist
		 * 
		*/
		
		//for(int i=0; i<collection.size(); i++){
		for (List<Attributes> list : collection) {
			for (Attributes attributes : list) {
				String pattern = attributes.getWord().toLowerCase();
				
				if(MatcherAux(pattern, stopwords))
					attributes.setStopword("STOPWORD");
				if(MatcherAux(pattern, dictionary))
					attributes.setKeyword("KEYWORD");
			}
		}
		
		return collection;
	}
	
	public List<List<Attributes>> labelling(List<List<Attributes>> collection, String gene, String label, int option) {
		/*
		 * This method receives the return of markWords.
		 * 
		 * This method labels the words as [String label];
		 * 
		 * The int option variable is defines bellow:
		 * 0 - Non of the following will be added.
		 * 1 - [RELATED, not-RELATED] - for the related field and 
		 * 2 - [LOWERCASE, UPPERCASE] - for the lowercase field.
		 * 3 - [RELATED, not-RELATED] and [LOWERCASE, UPPERCASE] will be added. 
		 * 
		*/
		
		for (List<Attributes> list : collection) {
			for (Attributes attributes : list) {
				if(attributes.getWord().equals(gene)){
					attributes.setLabel(label);
				}else{
					attributes.setLabel("non-GENE");
				}
				if(option == 2 || option == 3){
					if(attributes.getWord().equals(attributes.getWord().toLowerCase()))
						attributes.setLowercase("LOWERCASE");
					else
						attributes.setLowercase("UPPERCASE");
				}
				if(option == 1 || option == 3){
					if(attributes.getWord().equals(gene) || attributes.getKeyword() != null)
						attributes.setRelated("RELATED");
					else
						attributes.setRelated("not-RELATED");
				}
			}
		}
		
		return collection;
	}

	public ArrayList<String> sentenceBreaker(String input) {
		/*
		 * Method utilizes class SetenceBreaker.
		 * The class detects sentence boundaries and split the input string into paragraphs and sentences.
		*/
		SentenceBreaker spb = new SentenceBreaker();
		
		String out = spb.sentBreakerCaller(input); 
		
        StringTokenizer st = new StringTokenizer(out, "\n");
        ArrayList<String> sentence = new ArrayList<String>();
        
        while (st.hasMoreTokens()) {
            sentence.add(st.nextToken());
        }
        
        /*
        for (String string : sentence) {
			System.out.println("Sentence: "+ string);
		}
        */
		
		return sentence;
	}
	
	public String bufferReaderToString(BufferedReader input){
		String output = null;
		
		//Creating the string stopwords
				try {
					String line = input.readLine();
					while(line != null){
						if(output == null)
							output = line+" ";
						else
							output += line+" ";
						line = input.readLine();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		return output;
	}
	
	public List<Integer> getPositions(List<Attributes> collection, String gene){
		/*
		 * This method return all positions in the sentence where the String gene occurs.
		*/
		List<Integer> position = new ArrayList<Integer>();		//Position of each String gene within one sentence.
		
		int i = 0;
		for (Attributes attributes : collection) {
			if(attributes.getWord().contentEquals(gene))	//Acquiring the position in which the String gene is stored.
				position.add(i);							//Storing such position in the array position.
			i++;
		}
			
		return position;
	}
	
	public List<List<Attributes>> output(List<List<Attributes>> collection, String gene, int negWindow, int posWindow){
		List<List<Attributes>> newCollection = new ArrayList<List<Attributes>>();
		List<Integer> position;
		int initialPos, finalPosition;
		
		for (List<Attributes> list : collection) {
			position = getPositions(list, gene);
			int posZero = 0;
			for (int i = 0; i < position.size(); i++) {
				if(list.get(position.get(i)).getDistance() == 0)
					posZero = position.get(i);
			}
				initialPos = posZero - negWindow;
				initialPos = (initialPos+3 < 3)? 0 : initialPos;	//Making sure it doesn't accept negative numbers.
				
				finalPosition = posZero + posWindow;
				finalPosition = (finalPosition < list.size())? finalPosition : list.size();	//Making sure it doesn't go beyond the size of the 
																							//collection.
				
				newCollection.add(list.subList(initialPos, finalPosition));
		}
		
		return newCollection;
	}
	
	public static List<Attributes> cloneList(List<Attributes> list) {
	    List<Attributes> clone = new ArrayList<Attributes>(list.size());
	    for(Attributes item: list) clone.add(item.clone());
	    return clone;
	}

	public List<String> splitLines(BufferedReader stringFile) {
        ArrayList<String> sentences = new ArrayList<String>();

    	//Creating the string stopwords
		try {
			String line = stringFile.readLine();
			while(line != null){
				sentences.add(line);
				line = stringFile.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sentences;
	}
	
}
