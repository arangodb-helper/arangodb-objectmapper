package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
// Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
// Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.util.Iterator;

import org.arangodb.objectmapper.http.ArangoDbHttpResponse;
import org.arangodb.objectmapper.http.ResponseCallback;
import org.arangodb.objectmapper.jackson.ArangoDbDocument;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Cursor<T extends ArangoDbDocument> implements Iterator<T> {

	/**
	 * the logger
	 */

	private static Logger LOG = Logger
			.getLogger(Cursor.class);
	
	/**
	 * the cursor api path
	 */

	public final static String CURSOR_PATH = "/_api/cursor";

	/**
     * the ArangoDB database
     */

	private final Database database;
	
	
    /**
     * the class value type
     */

	private final Class<T> valueType;
	
    /**
     * the cached results
     */

	private ArrayNode resultNode = null;

    /**
     * has more results property 
     */

	private boolean hasNext = false;
	
    /**
     * cursor identifier 
     */

	private String id = "";
	
    /**
     * next result 
     */

	private int index = 0; 
	
    /**
     * count result 
     */

	private int count = 0; 
		
    /**
     * Creates cursor
     *
     * @param database     the ArangoDB database
     * @param query        the query 
     */

	public Cursor (Database database, final ArangoDbQuery<T> query) {
		this.database = database;
		this.valueType = query.getValueType();
		try {
			setValues(createCursor(query));
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
	}
	
    /**
     * returns true, if there a more results
     *  
     * @return true, if there are more results 
     */

	public boolean hasNext () {
		if (null == resultNode) {
			return false;
		}
		
		
		if (index < resultNode.size()) {
			return true;
		}
		
		if (hasNext) {
			// get more results from the server
			
			try {
				setValues(updateCursor(id));
			} catch (ArangoDb4JException e) {
				LOG.error("Cursor update failed!: " + e);
				e.printStackTrace();
				setValues(null);
			}		
			
			if (index < resultNode.size()) {
				return true;
			}
		}
		
		return false;
	}

    /**
     * returns the next result
     *  
     * @return Object 
     */

	public T next () {
		if (hasNext()) {
			
			try {
				return database.getSerializer().toObject(resultNode.get(index++), valueType);
			} catch (ArangoDb4JException e) {
				e.printStackTrace();
			}			
		}
		
		return null;
	}
	
    /**
     * updates the state
     *  
     * @param root         the cursor root node
     *  
     * @return true,       if the state was updated successfully 
     */

	private boolean setValues (JsonNode root) {
		if (root == null) {
			hasNext = false;
			id = "";
			index = 0;
			count = 0;
			return false;
		}
		
		if (root.has("result")) {
			JsonNode jn = root.get("result");
			if (jn.isArray()) {
				resultNode = (ArrayNode)jn;
			}
		}
		if (root.has("hasMore")) {
			hasNext = root.get("hasMore").asBoolean();
		}
		if (root.has("id")) {
			id = root.get("id").asText();
		}
		if (root.has("count")) {
			count = root.get("count").asInt();
		}
		
		index = 0;
		return true;
	}
	
    /**
     * deletes the cursor on the server
     */
	
	public void remove () {
		if (! hasNext()) {
			return;			
		}
		
		try {
			deleteCursor(id);
		} catch (ArangoDb4JException e) {
		}
		
		resultNode = null;
		hasNext = false;
		id = "";
		index = 0;
	}
	
    /**
     * Returns the count value
     */
	
	public int count() {
		return this.count;
	}
	
	private JsonNode createCursor (final ArangoDbQuery<T> query) throws ArangoDb4JException {
		String body = database.getSerializer().toJson(query.getAsMap());
		//System.out.println(body);
		return database.getRestHandler().post(database.buildPath(CURSOR_PATH), body,
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return database.getSerializer().toJsonNode(hr.getContentAsStream());
					}
				});
	}

	private JsonNode updateCursor (String id) throws ArangoDb4JException {
		return database.getRestHandler().put(database.buildPath(CURSOR_PATH) + "/" + id, "", 
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return database.getSerializer().toJsonNode(hr.getContentAsStream());
					}
				});
	}
	
	private void deleteCursor (String id) throws ArangoDb4JException {
                if (! id.equals("")) {
		        database.getRestHandler().delete(database.buildPath(CURSOR_PATH) + id);
                }
	}	
		

}
