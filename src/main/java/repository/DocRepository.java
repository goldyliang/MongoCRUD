package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * Interface for accessing MongoDB repository
 * 
 * Each document is represented through {@link StoredDocument}, with following fields
 *     - "id" : the id of the document
 *     - "document" : the raw content of the document with arbitrary scheme. Embedded document is supported.
 * 
 * A concrete object is to be instanced by the framework and wired to proper service bean(s)
 * 
 * @author goldyliang@gmail.com
 *
 */
@Repository
public interface DocRepository extends MongoRepository < StoredDocument, String> {
	// Nothing special to do here
}
