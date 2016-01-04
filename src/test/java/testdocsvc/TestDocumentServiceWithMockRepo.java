package testdocsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

import docservice.DocumentNotFoundException;
import docservice.DocumentService;
import repository.DocRepository;
import repository.StoredDocument;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Matchers.*;


/**
 * Unit test of service layer only, with mock repository.
 * This is mainly for demonstration as the current service layer is very thin.
 * 
 * This kind of unit test is especially valid if the logic in service layer is complicated.
 * 
 * Another way to test is to test the service layer together with the real repository.
 * 
 * @author goldyliang@gmail.com
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDocumentServiceWithMockRepo {
	
	/*
	 * Helper method to create a doc for testing
	 */
	private ModelMap createDoc (String name, String address, String content) {
        ModelMap doc = new ModelMap ();
        
        ModelMap embededDoc = new ModelMap ();
        embededDoc.addAttribute("name", name)
                  .addAttribute("address", address);
        
        doc.addAttribute("author", embededDoc);
        doc.addAttribute("content", content);
        return doc;
	}
	
	@Mock 
	private static DocRepository mockDocRepository;
	
	@InjectMocks
	private static DocumentService service = new DocumentService (mockDocRepository);
	
	/**
	 * Test method of {@link docservice.DocumentService#getDocumentById (String) getDocumentById}
	 * 
	 * @throws DocumentNotFoundException
	 */
	@Test
	public void testGetDocumentById () throws DocumentNotFoundException {
		
		String id = "abcde12355";
		
		StoredDocument doc = new StoredDocument (createDoc("Bahh", "unknown", "good book"));
        doc.setId(id);
        
		Mockito.when(mockDocRepository.findOne(id)).thenReturn(doc);

		StoredDocument retrievedDoc = service.getDocumentById(id);
		
		assertEquals (id, retrievedDoc.getId());
		assertEquals (doc.getDocument(), retrievedDoc.getDocument());
	}
	
	/**
	 * Test method of {@link docservice.DocumentService#getDocumentById (String) getDocumentById}
	 *
	 * With DocumentNotFoundException thrown if document not found
	 * 
	 */
	@Test (expected = DocumentNotFoundException.class)
	public void testGetDocumentById_NotFound () throws DocumentNotFoundException {
        
		String id = "1234";
		
		Mockito.when(mockDocRepository.findOne(id)).thenReturn(null);

		service.getDocumentById(id);

	}
	
	/**
	 * Test method of {@link docservice.DocumentService#getAllDocuments() getAllDocuments}
	 */
	@Test
	public void testGetAllDocuments() {
		List<StoredDocument> listDocs = new ArrayList <StoredDocument> ();
		
		listDocs.add (new StoredDocument (createDoc ("a","b","c")));
		listDocs.add (new StoredDocument (createDoc ("d","e","f")));
		
		Mockito.when(mockDocRepository.findAll()).thenReturn(listDocs);

		List<StoredDocument> listDocsRetrieved = service.getAllDocuments();
		
		assertEquals (listDocs.size(), listDocsRetrieved.size());
		
		// Verify all documents are equal
		Iterator <StoredDocument> iterOrig = listDocs.iterator();
		Iterator <StoredDocument> iterRetrieved = listDocsRetrieved.iterator();
		
		while (iterOrig.hasNext()) {
			assertEquals (iterOrig.next(), iterRetrieved.next());
		}
		
		// Test returning null list
		Mockito.when(mockDocRepository.findAll()).thenReturn(null);

		listDocsRetrieved = service.getAllDocuments();
		
		assertNull (listDocsRetrieved);	
	}
	
	/**
	 * Test method of {@link docservice.DocumentService#insertDocument() insertDocument}
	 */
	@Test
	public void testInsertDocument() {
		
		StoredDocument docRet = new StoredDocument (createDoc ("a","b","c"));
		docRet.setId("abcde");
		
		Mockito.when (mockDocRepository.insert( any(StoredDocument.class)))
		       .thenReturn (docRet);

		String id = service.insertDocument(docRet.getDocument());
		
		assertEquals ("abcde", id);
		
		// Test insert with null document
		assertNull (service.insertDocument(null));
	}
	
	/**
	 * Test method of {@link docservice.DocumentService#updateDocument() updateDocument}
	 * @throws DocumentNotFoundException
	 */
	@Test
	public void testUpdateDocument () throws DocumentNotFoundException {
		
		/* update if document present */
		String id = "abcde";
		StoredDocument docRet = new StoredDocument (createDoc ("a","b","c"));
		docRet.setId(id);
		
		StoredDocument docNew = new StoredDocument (createDoc ("d", "e", "ef"));
		docNew.setId(id);
		
		Mockito.when (mockDocRepository.findOne(id))
	       .thenReturn (docRet);
		
		Mockito.when (mockDocRepository.save( any(StoredDocument.class)))
	       .thenReturn (docNew);
		
		service.updateDocument(id, docNew.getDocument());
		
	}
	
	/**
	 * Test method of {@link docservice.DocumentService#updateDocument() updateDocument}
	 * 
	 * When the document is not present
	 * 
	 * @throws DocumentNotFoundException
	 */
	@Test (expected = DocumentNotFoundException.class)
	public void testUpdateDocument_NotFound () throws DocumentNotFoundException {
		
		/* update if document present */
		String id = "abcde";
		
		Mockito.when (mockDocRepository.findOne(id))
	       .thenReturn (null);
		
		service.updateDocument(id, createDoc("a","b","c"));
		
	}
	
}
