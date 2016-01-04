package repository;

import org.springframework.data.annotation.Id;
import org.springframework.ui.ModelMap;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document class wrapping the document id and the raw document contents represented as {@link ModelMap}.
 * 
 * @author goldyliang@gmail.com
 *
 */
@Document (collection = "entries")
public class StoredDocument {

	@Id
	private String id;
	
	private ModelMap document;
	
	public StoredDocument () {}
	
	/**
	 * Construct a document with raw content and null id
	 * @param doc Raw content of the document
	 */
	public StoredDocument(ModelMap doc) {this.document  = doc;}
	
	/**
	 * Set the id
	 */
	public void setId (String id) { this.id = id;}
	
	/**
	 * Set the document
	 */
	public void setDocument ( ModelMap doc) {this.document = doc;}
	
	/**
	 * Get the ID
	 */
	public String getId () {return id;}
	
	/**
	 * Get the document
	 */
	public ModelMap getDocument () { return document; }
}
