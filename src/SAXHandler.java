/**
 * This class modifies the default SAX parser handler
 * 
 */

/**
 * @author Daniel Jean
 *
 */
//Default package

import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler{

	String TAG_DOCUMENT = "doc";
	String TAG_ARRAY = "arr";
	String TAG_PUBMED_CONTENT = "str";	
	String ATRIBUTE_ABSTRACT = "medline_abstract_text";
	String ATRIBUTE_JOURNAL = "medline_journal_title";
	String ATRIBUTE_TITLE = "medline_article_title";
	String ATRIBUTE_ID = "id";

	boolean flag_abstract_txt;
	boolean flag_abstract_part;
	boolean flag_journal;
	boolean flag_title;
	boolean flag_id;
	boolean flag_doc;
	
	String gene;

	private Stack<PubmedDocument> documentStack = new Stack<PubmedDocument>();
	public StringBuilder content = new StringBuilder();

	public SAXHandler(String targetWord) {
		flag_abstract_txt = false;
		flag_journal = false;
		flag_title = false;
		flag_id = false;
		gene = targetWord;
		
		flag_doc = false;
	}

	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		if (qName.equals(TAG_DOCUMENT)) {//<doc>
			PubmedDocument currentDocument = new PubmedDocument();
			documentStack.push(currentDocument);
			
			flag_doc = true;
		}

		if(qName.equals(TAG_ARRAY)){//arr
			//This case is when the xml has an array of info
			if(attributes.getValue("name").equals(ATRIBUTE_ABSTRACT)){//name = medline_abstract_text
				flag_abstract_txt = true;	
			}
		}

		if (qName.equals(TAG_PUBMED_CONTENT)){//str
	
			if(attributes.getLength()!=0){
				if(attributes.getValue("name").equals(ATRIBUTE_JOURNAL)){//str = medline_journal_title
					flag_journal = true;
				}
				if (attributes.getValue("name").equals(ATRIBUTE_TITLE)){//str = medline_article_title
					flag_title = true;
				}
				if (attributes.getValue("name").equals(ATRIBUTE_ID)){//str = id
					flag_id = true;
				}
			}else{
				//This is to indicate that the xml file can have an array of str's with info
				if(flag_abstract_txt){ // if we are inside arr
				flag_abstract_part = true;
			}
			}
		}

	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		//This ensures that only the result node is appended
		if(flag_doc){
			content.append(new String(ch, start, length)); //Appending nodes
		}
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equals(TAG_PUBMED_CONTENT)){
			if(documentStack!=null && !documentStack.isEmpty()){ //we are inside a document element
				PubmedDocument currentDocument = documentStack.peek();
				if(flag_abstract_part){
					
					//Setting temp to start at the 11th character to avoid spaces and line breaks.
					String temp = content.toString().substring(10);
					
					if(currentDocument.getAbstract_text() != null){//To avoid adding null to the result.
						currentDocument.setAbstract_text(currentDocument.getAbstract_text() + temp);
					}else{
						currentDocument.setAbstract_text(temp);
					}
					flag_abstract_part = false;
				}
				else if(flag_journal){
					String temp = content.toString().replace("\n", "");
					temp = temp.substring(8); //removing space and line breaks
					currentDocument.setJournal(temp);
					flag_journal = false;
				}else if(flag_title){
					//Setting temp to start at the 9th character to avoid spaces and line breaks.
					String temp = content.toString().substring(8);
					
					currentDocument.setTitle(temp);
					flag_title = false;
				}else if(flag_id){
					String temp = content.toString().replace("\n", "");
					temp = temp.substring(4);
					currentDocument.setId(temp);
					flag_id = false;
				}
				content = new StringBuilder();
			}
		}else if(qName.equals(TAG_ARRAY)){
			flag_abstract_txt = false;
		}else if (qName.equals(TAG_DOCUMENT)) {//<doc>
			flag_doc = false;
		}
	}

	public List<PubmedDocument> getResults(){
		return  documentStack;
	}
	
	
	public void print(List<PubmedDocument> documents) {
		for (PubmedDocument doc : documents){
		      System.out.println(doc);
		}	
	}
}


