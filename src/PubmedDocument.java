
public class PubmedDocument {

	private String abstract_text;
	private String title;
	private String journal;
	private String id;
	
	public String getAbstract_text() {
		return abstract_text;
	}
	public void setAbstract_text(String abstract_text) {
		this.abstract_text = abstract_text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {	
		return "[START]\nID: " + id +"\n" + "Title:\n" + title + "\nAbstract:\n" + abstract_text+"\n[END]\n";
	}
}
