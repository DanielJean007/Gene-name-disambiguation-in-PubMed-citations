
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Array;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		String gene = "T";
		String path = "/home/danieljean/Desktop/Project/Data/"+gene+"/"; 

		Util<?> util = new Util<Object>();
		
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = parserFactor.newSAXParser();
	    SAXHandler handler = new SAXHandler(gene);
	    
	    
	    //XML file that contains the docs. 
	    parser.parse(path+gene+".xml",handler);
	    
	    //Documents placed in a list of PubmedDocument type.
	    List<PubmedDocument> result = handler.getResults();
	    
	    //Save the abstracts in txt extension - Start.
	    List<String> files = new ArrayList<String>();
	    for (PubmedDocument file : result) {
	    	if(file.getAbstract_text() != null)// || file.getTitle() != null)
	    		files.add(file.getAbstract_text()+"\n");
		}
	    
	    /*
	    //To read the file that contains the stopwords.
	    BufferedReader stopwords = new BufferedReader
	    		(new FileReader("/home/danieljean/Desktop/Project/Data/smartStopMod.wds"));
	    
	    //To read the file that contains the dictionary of the word of interest.
	    BufferedReader dictionary = new BufferedReader
	    		(new FileReader("/home/danieljean/Desktop/Project/Data/"+gene+"/dict.dx"));
	    
	    String stringStopwords = util.bufferReaderToString(stopwords);
	    String stringDictionay = util.bufferReaderToString(dictionary);
	    
	    stopwords.close();
	    dictionary.close();
	    */
	    
	    List<List<Attributes>> collection = null;
	    StringBuilder input = new StringBuilder();
	    List<String> sentences;
	    for (String file : files) {
	    	sentences = util.sentenceBreaker(file);
	    	collection = util.stringToCollection(sentences, gene);
	    	//collection = util.calcRelDist(collection, gene);
	    	//collection = util.markWords(collection, stringStopwords, stringDictionay);
			//collection = util.output(collection, gene, 3, 3);
			
			for (List<Attributes> list : collection) {
				input.append("*************** New sentence:***************\n");
				for (Attributes attributes : list) {
					input.append(attributes.getWord()+"\n");
				}
			}
			input.append("############ New file:###################\n");
		}
	    
	    util.saveFile(input.toString(), path, "sample.txt");
	    
	    
	    
	    /*
	    //To read the file that contains the sentences and to label them. - Start
	    BufferedReader br = new BufferedReader
	    		(new FileReader("/home/danieljean/Desktop/Project/Data/"+gene+"/non-geneSamples.txt"));
	    List<String> file = util.splitLines(br);
	    br.close();
	    
	    List<List<Attributes>> collection = null;
	    StringBuilder input = new StringBuilder();
	    
	    collection = util.stringToCollection(file, gene);
	    collection = util.calcRelDist(collection, gene);
	    collection = util.markWords(collection, stringStopwords, stringDictionay);
	    collection = util.labelling(collection, gene, "non-GENE", 3);
	    collection = util.output(collection, gene, 5, 5);
	    for (List<Attributes> list : collection) {
			for (Attributes attributes : list) {
				input.append(attributes+"\n");
			}
		}
	    
	    util.saveFile(input.toString(), path, "sample5x5-sw-re.txt");
	    //To read the file that contains the sentences and to label them. - End
	    */
	    
	    /*
	    //Save the sentences that have the String gene - Start.
	    List<List<Attributes>> collection = null;
	    StringBuilder input = new StringBuilder();
	    for (String file : files) {
	    	collection = util.stringToCollection(file, gene);
	    	collection = util.calcRelDist(collection, gene);
			collection = util.output(collection, gene, 6, 6);
			
			for (List<Attributes> list : collection) {
				for (Attributes attributes : list) {
					input.append(attributes.getWord()+" ");
					
				}
				input.append("\n");
				System.out.println(input);
				Scanner scan = new Scanner(System.in);
	    		int sc = scan.nextInt();
			}
		}
	    util.saveFile(input.toString(), path, "sentencesToLabel.txt");
	    */
	    
	    result.clear();
	    //resultDocwithGene.clear();
	    //collection.clear();
	    System.exit(0);
	}
}
