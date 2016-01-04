package restservice;

import org.springframework.data.annotation.Id;
import org.springframework.ui.ModelMap;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "entries")
public class StoredDocument {

	@Id
	private String id;
	
	private ModelMap document;
	
	public StoredDocument () {}
	
	public StoredDocument(ModelMap doc) {this.document  = doc;}
	
	public void setId (String id) { this.id = id;}
	
	public void setDocument ( ModelMap doc) {this.document = doc;}
	
	public String getId () {return id;}
	
	public ModelMap getDocument () { return document; }
}
