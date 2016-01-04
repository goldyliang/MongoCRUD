package docservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import repository.DocRepository;
import repository.StoredDocument;

/**
 * 
 * Service for CRUD operations for documents towards a data repository (Mongodb, for example)
 * 
 * Each document to be stored is composed with:
 *  - A field name "id" (String, auto-generated)
 *  - Raw document named "document".
 *
 *    The raw document can be in arbitrary scheme, represented by the map class of {@link ModelMap}
 *    Fields in raw document can be embedded with other documents.
 *    
 * @author goldyliang@gmail.com
 *
 */
@Service public class DocumentService {

	private DocRepository docRepository;
	
	@Autowired
	public DocumentService (DocRepository docRep) {
		this.docRepository = docRep;
	}
	
	/**
	 * Insert a raw document in the repository, and auto-generate an ID
	 * @param doc The raw document provided in the form of <String, Object> map.
	 *            Embedded documents are supported.
	 * @return The auto-generated document ID in String. Or NULL if doc == NULl
	 */
	public String insertDocument (ModelMap doc) {
		
		if (doc == null) return null;
		
    	StoredDocument myDoc = new StoredDocument (doc);
    	
    	StoredDocument newDoc =  docRepository.insert(myDoc);
    	return newDoc.getId();
	}
	
	/**
	 * Update an existing document provided a specific id
	 * 
	 * @param id The ID of the document which is to be updated
	 * @param doc The raw document to update.
	 * @throws DocumentNotFoundException  if the document with id is not found.
	 */
	public void updateDocument (String id, ModelMap doc) throws DocumentNotFoundException {
		
		if (docRepository.findOne(id) == null)
			throw new DocumentNotFoundException();
		
    	StoredDocument myDoc = new StoredDocument (doc);

    	myDoc.setId(id);
    	
		docRepository.save (myDoc);
	}
	
	/**
	 * Delete an existing document with a specific document ID
	 * 
	 * @param id The ID of the document which is to be deleted
	 * @throws DocumentNotFoundException If the document is not found
	 */
	public void deleteDocument (String id)  throws DocumentNotFoundException {
		
		if (docRepository.findOne(id) == null)
			throw new DocumentNotFoundException();
		
    	docRepository.delete(id);
	}
	
	/**
	 * Get all documents and fill it in a list of type {@link StoredDocument}
	 * 
	 * @return The list of documents, wrapped with the raw documents and their IDs
	 *         Return NULL if no documents found
	 */
	public List <StoredDocument> getAllDocuments () {
		
		List <StoredDocument> listDocs = docRepository.findAll();
		
		return listDocs;
	}
	
	/**
	 * Get a document with a specific ID.
	 * 
	 * @param id The Id of the document to be retrieved
	 * @return The document object wrapped with the ID and the raw document.
	 * @throws DocumentNotFoundException If the document is not found
	 */
	public StoredDocument getDocumentById (String id) throws DocumentNotFoundException {
		StoredDocument doc = docRepository.findOne (id);
		
		if (doc == null) 
			throw new DocumentNotFoundException();
		else
			return doc;
	}
}
