package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
// Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
// Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import org.arangodb.objectmapper.jackson.ArangoDbDocument;

/**
 * Simple object repository
 * 
 * @author abrandt
 *
 * @param <T>
 */

public class ArangoDbRepository<T extends ArangoDbDocument> {

	/**
     * the ArangoDB database
     */

	private final Database database;
		
    /**
     * the class value type
     */

	private final Class<T> valueType;
	
    /**
     * Constructor
     *
     * @param database     the ArangoDB database
     * @param query        the query 
     */

	public ArangoDbRepository (Database database, Class<T> valueType) {
		this.database = database;
		this.valueType = valueType;
		
		createCollectionIfNotPresent();
		createIndexes();
	}
	
    /**
     * Creates the collection for the class type
     * 
     * Overwrite this function to create the class with non standard options
     */

	protected void createCollectionIfNotPresent() {
		// 
		try {
			database.createCollection(valueType);
		} catch (ArangoDb4JException e) {
		}
	}
	
    /**
     * Creates the indexes 
     * 
     * Overwrite this function to create additional indexes
     */

	protected void createIndexes() {
		// overwrite this for non std indices		
	}

	///////////////////////////////////////////////////////////////////////////
	// CRUD functions
	///////////////////////////////////////////////////////////////////////////	
	
    /**
     * Save the object to the class collection. 
     * 
     * @param o     The object
     * 
     * @return T    The object
     * 
     * @throws ArangoDb4JException
     * 				on error
     */

	public T create(T o) throws ArangoDb4JException {
		return database.createDocument(o);
	}
	
    /**
     * Loads an object by key. 
     * 
     * @param key   A key
     * 
     * @return T    The object
     * 
     * @throws ArangoDb4JException
     * 				on error
     */

	public T read(String key) throws ArangoDb4JException {
		return database.readDocument(valueType, key);
	}
	
    /**
     * Updates an object 
     * 
     * @param o   The object
     * 
     * @return T    The object
     * 
     * @throws ArangoDb4JException
     * 				on error
     */

	public T update(T o) throws ArangoDb4JException {
		return database.updateDocument(o);
	}
	
    /**
     * Delete an object 
     * 
     * @param o   The object
     * 
     * @throws ArangoDb4JException
     * 				on error
     */

	public void delete(T o) throws ArangoDb4JException {
		database.deleteDocument(o);
	}

    /**
     * Get all objects 
     * 
     * @return Cursor<T>    A cursor (iterator)
     * 
     * @throws ArangoDb4JException
     * 				on error
     */

	public Cursor<T> getAll() throws ArangoDb4JException {
		return database.getAll(valueType);
	}
		
    /**
     * Get all objects filtered by one key/value 
     * 
     * @param key     The object attribute
     * @param value   The attribute value
     * 
     * @return Cursor<T>    A cursor (iterator)
     * 
     * @throws ArangoDb4JException
     * 				on error
     */

	public Cursor<T> getAllByKeyValue(String key, Object value) {
		ArangoDbQuery<T> query = getQuery();
		query.has(key, value);
		return query.execute();
	}

    /**
     * Get query object for more complex queries 
     * 
     * @return ArangoDbQuery<T>    A query
     */

	public ArangoDbQuery<T> getQuery() {
		return database.getQuery(valueType);
	}
	
}
