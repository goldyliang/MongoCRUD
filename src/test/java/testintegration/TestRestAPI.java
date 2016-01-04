package testintegration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import repository.DocRepository;
import repository.StoredDocument;
import restapi.Application;
import restapi.DocIDReturn;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TestRestAPI {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    
	@Autowired
	private DocRepository docRepository;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;

	List <StoredDocument> preAddedDocs;
	
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }
	
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
	
	
	private ModelMap createDoc (String name, String address, String content) {
        ModelMap doc = new ModelMap ();
        
        ModelMap embededDoc = new ModelMap ();
        embededDoc.addAttribute("name", name)
                  .addAttribute("address", address);
        
        doc.addAttribute("author", embededDoc);
        doc.addAttribute("content", content);
        return doc;
	}
	
	private void addDoc (String name, String address, String content) {
		
		StoredDocument doc = new StoredDocument (createDoc (name,address,content));
		
        preAddedDocs.add ( docRepository.save(doc));
	}
	
	@SuppressWarnings("unchecked")
	private ResultActions verifyDoc (
			ResultActions r, 
			int i,
			StoredDocument doc) throws Exception {
		String id = doc.getId();
		String content = (String) doc.getDocument().get("content");
		Map <String, Object> auth = (Map<String, Object>) doc.getDocument().get("author");
		String name = (String) auth.get("name");
		String address = (String) auth.get("address");
		
		String pathPrefix = (i>=0? "$[" + i + "]" : "$");

        return r.andExpect(status().isOk())
        		.andExpect(content().contentType(contentType))
        		.andExpect(jsonPath( pathPrefix + ".id", is(id)))
        		.andExpect(jsonPath( pathPrefix + ".document.content", is(content)))
        		.andExpect(jsonPath( pathPrefix + ".document.author.name", is(name)))
        		.andExpect(jsonPath( pathPrefix + ".document.author.address", is (address)));
	}
	
    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();

        docRepository.deleteAll();

        preAddedDocs = new ArrayList <StoredDocument> ();
    	
        addDoc ("Gordon", "6955 fielding", "Here is the content");
        addDoc ("Betty", "6951 fielding", "Another content");
    }
	
    @Test
	public void testGetAllDoc () throws Exception {
		
        ResultActions r = mockMvc.perform(get("/restAPI/items/"));
        
        r.andExpect(status().isOk())
        .andExpect(content().contentType(contentType));
        
        for (int i = 0; i < preAddedDocs.size(); i++)
        	verifyDoc (r, i, preAddedDocs.get(i));
 	}
    
	@Test
	public void testGetOneDoc () throws Exception {
		
		StoredDocument doc = preAddedDocs.get(0);
		String id = doc.getId();
		
        ResultActions r = mockMvc.perform(get("/restAPI/items/" + id));
        
        r.andExpect(status().isOk())
        .andExpect(content().contentType(contentType));
        
        verifyDoc (r, -1, doc);
        
        doc = preAddedDocs.get(1);
		id = doc.getId();
		
        r = mockMvc.perform(get("/restAPI/items/" + id));
        
        r.andExpect(status().isOk())
        .andExpect(content().contentType(contentType));
        
        verifyDoc (r, -1, doc);
        
	}
	
	@Test
	public void testGetOneDocNotFound () throws Exception {
		
		StoredDocument doc = preAddedDocs.get(0);
		String id = doc.getId();
		
		id = id.substring(0, id.length()-1);
		
        ResultActions r = mockMvc.perform(get("/restAPI/items/" + id));
        
        r.andExpect(status().isNotFound());
	}
	
	
	@Test
	public void testPostOneNewDoc () throws Exception {
		
		ModelMap newDoc = createDoc("Bach", "unknonwn", "Wonderful");
		
		String jsonNewDoc = json(newDoc);
		
		MvcResult result = 
				this.mockMvc.perform(post("/restAPI/items/")
					.contentType(contentType)
					.content(jsonNewDoc))
        			.andExpect(status().isCreated())
        			.andReturn();
	    
		String resultJson = result.getResponse().getContentAsString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		DocIDReturn idRet = mapper.readValue(resultJson, DocIDReturn.class);
		
        assertEquals (3, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(idRet.getId());
        
        assertEquals (newDoc, retrievedDoc.getDocument());		
		
	}
	
	@Test
	public void testPutOneNewDoc () throws Exception {
		
		ModelMap newDoc = createDoc("Bach", "unknonwn", "Wonderful");
		
		String jsonNewDoc = json(newDoc);
		
		MvcResult result = 
				this.mockMvc.perform(put("/restAPI/items/")
					.contentType(contentType)
					.content(jsonNewDoc))
        			.andExpect(status().isCreated())
        			.andReturn();
	    
		String resultJson = result.getResponse().getContentAsString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		DocIDReturn idRet = mapper.readValue(resultJson, DocIDReturn.class);
		
        assertEquals (3, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(idRet.getId());
        
        assertEquals (newDoc, retrievedDoc.getDocument());		
		
	}
	
	@Test
	public void testPostUpdateDoc () throws Exception {
		
		ModelMap newDoc = createDoc("Bach", "unknonwn", "Wonderful");
		
		String jsonNewDoc = json(newDoc);
		
		String id = this.preAddedDocs.get(0).getId();
		
		this.mockMvc.perform(post("/restAPI/items/" + id)
			.contentType(contentType)
			.content(jsonNewDoc))
			.andExpect(status().isOk());
	    
        assertEquals (2, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(id);
        
        assertEquals (newDoc, retrievedDoc.getDocument());		
		
	}
	
	@Test
	public void testPostUpdateDocNotFound () throws Exception {
		
		ModelMap newDoc = createDoc("Bach", "unknonwn", "Wonderful");
		
		String jsonNewDoc = json(newDoc);
		
		String id = this.preAddedDocs.get(0).getId();
		String fakeId = id.substring(0, id.length()-1);
		
		this.mockMvc.perform(post("/restAPI/items/" + fakeId)
			.contentType(contentType)
			.content(jsonNewDoc))
			.andExpect(status().isNotFound());
	    
        assertEquals (2, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(id);
        
        assertEquals (preAddedDocs.get(0).getDocument(), retrievedDoc.getDocument());		
		
	}
	
	@Test
	public void testPutUpdateDoc () throws Exception {
		
		ModelMap newDoc = createDoc("Bach", "unknonwn", "Wonderful");
		
		String jsonNewDoc = json(newDoc);
		
		String id = this.preAddedDocs.get(0).getId();
		
		this.mockMvc.perform(put("/restAPI/items/" + id)
			.contentType(contentType)
			.content(jsonNewDoc))
			.andExpect(status().isOk());
	    
        assertEquals (2, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(id);
        
        assertEquals (newDoc, retrievedDoc.getDocument());		
		
	}
	
	@Test
	public void testDeleteDoc () throws Exception {
		
		String id = this.preAddedDocs.get(1).getId();
		
		this.mockMvc.perform(delete("/restAPI/items/" + id)
								.contentType(contentType))
			.andExpect(status().isOk());
	    
        assertEquals (1, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(id);
        
        assertNull (retrievedDoc);		
		
	}
	
	@Test
	public void testDeleteDocNotFound () throws Exception {
		
		String id = this.preAddedDocs.get(1).getId();
		
		String fakeId = id.substring(0, id.length()-1);
		
		this.mockMvc.perform(delete("/restAPI/items/" + fakeId)
								.contentType(contentType))
			.andExpect(status().isNotFound());
	    
        assertEquals (2, docRepository.count());
        
        StoredDocument retrievedDoc = docRepository.findOne(id);
        
        assertEquals (preAddedDocs.get(1).getDocument(), retrievedDoc.getDocument());		
		
	}
}
