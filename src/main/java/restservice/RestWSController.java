package restservice;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController 
@RequestMapping ("/restAPI/items")
public class RestWSController {

    private DocumentService serviceDoc;
    
    @Autowired
    public RestWSController (DocumentService service) {
    	System.out.println("Construction");
    	serviceDoc = service;
    }

    @ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR,reason = "Internal error")
    @ExceptionHandler(Exception.class)
    public void exceptionHandler(Exception e) 
    {
    	e.printStackTrace();
    }
    
    @RequestMapping(method={RequestMethod.POST, RequestMethod.PUT}) 
    public ResponseEntity<DocIDReturn> insertDoc (
    		@RequestBody ModelMap doc) {
    	
    	String id = serviceDoc.insertDocument(doc);
    	
    	return new ResponseEntity<DocIDReturn> (
    			new DocIDReturn (id), HttpStatus.CREATED);
    }
    
    @RequestMapping(value="/{id}", method={RequestMethod.POST, RequestMethod.PUT}) 
    public ResponseEntity<StoredDocument> updateDoc (
    		@PathVariable String id,
    		@RequestBody ModelMap doc) {
    	
    	try {
    		serviceDoc.updateDocument(id, doc);
    	} catch (DocumentNotFoundException e) {
    		return new ResponseEntity<StoredDocument>(HttpStatus.NOT_FOUND);
    	}
    	
    	return new ResponseEntity<StoredDocument>(HttpStatus.OK);
    }
    
    @RequestMapping(value="/{id}", method={RequestMethod.DELETE}) 
    public ResponseEntity<StoredDocument> deleteDoc (@PathVariable String id) 
    	throws DocumentNotFoundException {
    	
    	try {
        	serviceDoc.deleteDocument(id);
    	} catch (DocumentNotFoundException e) {
    		return new ResponseEntity<StoredDocument>(HttpStatus.NOT_FOUND);
    	}
    	
    	return new ResponseEntity<StoredDocument>(HttpStatus.OK);
    	
    }
    
    @RequestMapping(value="/{id}", method={RequestMethod.GET}) 
    public ResponseEntity<StoredDocument> getDocById (@PathVariable String id) {
    	
    	try {
        	return new ResponseEntity<StoredDocument>(
        			serviceDoc.getDocumentById(id),
        			HttpStatus.OK);
        	
    	} catch (DocumentNotFoundException e) {
    		return new ResponseEntity<StoredDocument>(HttpStatus.NOT_FOUND);
    	}
    	    	
    }
    
    @RequestMapping(method={RequestMethod.GET}) 
    public List<StoredDocument> getAllDoc () {
    	List <StoredDocument> docs = serviceDoc.getAllDocuments();
    	
    	return docs;
    }
	    

}
