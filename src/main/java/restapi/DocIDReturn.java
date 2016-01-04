package restapi;


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