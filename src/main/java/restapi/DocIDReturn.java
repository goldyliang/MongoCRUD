package restapi;

/**
 * Wrapper class for operations where a document ID is to be returned to REST client
 * 
 * @author goldyliang@gmail.com
 *
 */
public class DocIDReturn {
	private String id;
	
	public DocIDReturn () {}
	
	public DocIDReturn (String id) {this.id = id;}
	
	public String getId () { 
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
}