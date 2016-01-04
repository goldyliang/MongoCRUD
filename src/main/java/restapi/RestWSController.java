package restapi;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import docservice.DocumentNotFoundException;
import docservice.DocumentService;
import repository.StoredDocument;

/**
 * RESTful Spring MVC controller, for a service of JSON document CRUD operations
 * 
 * @author goldyliang@gmail.com
 *
 */
@RestController 
@RequestMapping ("/restAPI/items")
public class RestWSController {

    private DocumentService serviceDoc;
    
    @Autowired
    public RestWSController (DocumentService service) {
    	serviceDoc = service;
    }

    /**
     * Exception handling for DocumentNotFoundException
     * Return HTTP status with NOT_FOUND (404) and proper information
     */
    @ResponseStatus(code=HttpStatus.NOT_FOUND,reason = "Specified Document not found")
    @ExceptionHandler(DocumentNotFoundException.class)
    public void exceptionHandler(DocumentNotFoundException e) { }
    
    /**
     * Exception handling for HttpMessageNotReadableException (invalid body data)
     * Return HTTP status with BAD_REQUEST (400) and proper information
     */
    @ResponseStatus(code=HttpStatus.BAD_REQUEST,reason = "Invalid Http message")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void exceptionHandler(HttpMessageNotReadableException e) { }
    
    /**
     * Exception handling for all other exceptions except for DocumentNotFoundException and 
     * HttpMessageNotReadableException.
     * Return HTTP status with INTERNAL_SERVER_ERROR (500).
     * @param e The exception
     */
    @ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR,reason = "Internal error")
    @ExceptionHandler(Exception.class)
    public void exceptionHandler(Exception e) 
    {
    	// Print out exception information
    	// TODO: log it using logging system
    	e.printStackTrace();
    }
    
    /**
     * Insert a new document with either POST or PUT without ID specified.
     * Raw document content is provided in the body in the form of JSON.
     * 
     * Return HTTP status 201 Created if success.
     * And return the generated document ID in the form of JSON:
     * 
     *     { "id" : generated_id }
     * 
     * Refer to exceptionHandler(s) for status codes to be returned if any error.
     * 
     * @param doc Raw document provided in the body of request
     * @return Document ID and HTTP status of 201 Created, or error information
     */
    @RequestMapping ( method={RequestMethod.POST, RequestMethod.PUT} ) 
    public ResponseEntity<DocIDReturn> insertDoc (
    		@RequestBody ModelMap doc) {
    	
    	String id = serviceDoc.insertDocument(doc);
    	
    	return new ResponseEntity<DocIDReturn> (
    			new DocIDReturn (id), HttpStatus.CREATED);
    }
    
    /**
     * Update an existing document with either POST or PUT request, provided with a document ID.
     * 
     * Raw document content is provided in the body in the form of JSON.
     * 
     * Return HTTP status 200 OK and an empty body if success.
     * Refer to exceptionHandler(s) for status codes to be returned if any error.
     * 
     * @param id  Document id provided in the URL
     * @param doc Raw document provided in the body of request
     * @return HTTP status of 200 OK or error information.
     * @throws DocumentNotFoundException (to be handled in exception handler)
     */
    @RequestMapping( 
    		value="/{id}", 
    		method={RequestMethod.POST, RequestMethod.PUT}) 
    public void updateDoc (
    		@PathVariable String id,
    		@RequestBody ModelMap doc) throws DocumentNotFoundException {
    	    	
    	serviceDoc.updateDocument(id, doc);
    }
    
    /**
     * Delete a document with provided id in the URL
     * 
     * Return HTTP status of 200 OK and empty body if deleted.
     * Refer to exceptionHandler(s) for status codes to be returned if any error.
     * 
     * @param id Document id provided in the URL
     * @return HTTP status of 200 OK or error information.
     * @throws DocumentNotFoundException (to be handled in exception handler)
     */
    @RequestMapping(
    		value="/{id}", 
    		method={RequestMethod.DELETE}) 
    public void deleteDoc (
    		@PathVariable String id) 
    	throws DocumentNotFoundException {
    	
        serviceDoc.deleteDocument(id);
    }
    
    /**
     * Retrieve a document by the id provided in the URL
     * The document is returned in the form of JSON as below:
     * 
     * { "id" : document id, 
     *   "document" : {
     *                  ...
     *                }
     * }
     * 
     * Return HTTP status of 200 OK and empty body if deleted.
     * Refer to exceptionHandler(s) for status codes to be returned if any error.
     * 
     * @param id The id of the document to be retrieved
     * @return The document to be retrieved with document ID and raw content
     * @throws DocumentNotFoundException (to be handled in exception handler)
     */
    @RequestMapping(
    		value="/{id}", 
    		method={RequestMethod.GET}) 
    public StoredDocument getDocById (
    		@PathVariable String id) 
    	throws DocumentNotFoundException {
    	
    	return serviceDoc.getDocumentById(id);
    }
    
    /**
     * Get all documents from the repository.
     * 
     * The documents are returned as an JSON array of document IDs and raw contents, as below:
     * 
     * [ { "id" : id for document#1,
     *     "document" : { ... }
     *   }
     *   { "id" : id for document#2,
     *     "document" : { ... }
     *   }
     *   ...
     * ]
     * 
     * Return [] if no any documents.
     * 
     * Return HTTP status of 200 OK if no error.
     * Refer to exceptionHandler(s) for status codes to be returned if any error.
     *  
     * @return The list of documents with IDs and raw contents.
     */
    @RequestMapping(method={RequestMethod.GET}) 
    public List<StoredDocument> getAllDoc () {
    	List <StoredDocument> docs = serviceDoc.getAllDocuments();
    	
    	return docs;
    }
	    

}
