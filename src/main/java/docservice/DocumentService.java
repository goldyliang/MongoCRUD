package docservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import repository.DocRepository;
import repository.StoredDocument;


@Service
public class DocumentService {

	@Autowired
	DocRepository docRepository;
	
	public static class GeneralReturn {
		
		String id;
	}
	
	public String insertDocument (ModelMap doc) {
    	StoredDocument myDoc = new StoredDocument (doc);
    	return docRepository.insert(myDoc).getId();
	}
	
	public void updateDocument (String id, ModelMap doc) throws DocumentNotFoundException {
		
		if (docRepository.findOne(id) == null)
			throw new DocumentNotFoundException();
		
    	StoredDocument myDoc = new StoredDocument (doc);

    	myDoc.setId(id);
    	
		docRepository.save (myDoc);
	}
	
	public void deleteDocument (String id)  throws DocumentNotFoundException {
		
		if (docRepository.findOne(id) == null)
			throw new DocumentNotFoundException();
		
    	docRepository.delete(id);
	}
	
	public List <StoredDocument> getAllDocuments () {
		
		return docRepository.findAll();
	}
	
	public StoredDocument getDocumentById (String id) throws DocumentNotFoundException {
		StoredDocument doc = docRepository.findOne (id);
		
		if (doc == null) 
			throw new DocumentNotFoundException();
		else
			return doc;
	}
}
