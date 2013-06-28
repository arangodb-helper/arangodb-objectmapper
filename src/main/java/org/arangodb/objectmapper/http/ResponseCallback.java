package org.arangodb.objectmapper.http;

import org.arangodb.objectmapper.ArangoDb4JException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * Original file from "Java API for CouchDB http://www.ektorp.org"
 * 
 * @author henrik lundgren
 * 
 */

public class ResponseCallback<T> {
		
	/**
	 * Error handler (throws ArangoDb4JException)
	 * 
	 * @param hr
	 *            Request response
	 *            
	 * @return T
	 * 
	 * @throws ArangoDb4JException
	 */

	public T error(ArangoDbHttpResponse hr) throws ArangoDb4JException {
		ObjectMapper om = new ObjectMapper();
		
		JsonNode root; 
		
		try {
			root = om.readTree(hr.getContentAsStream());
		}
		catch (Exception e) {
			throw new ArangoDb4JException(e.getMessage(), hr.getCode(), 0);
		}
		
		String errorMessage = root.has("errorMessage") ? root.get("errorMessage").asText() : null;
		Integer errorNum = root.has("errorNum") ? root.get("errorNum").asInt() : null;
		
		throw new ArangoDb4JException(errorMessage, hr.getCode(), errorNum);
	}

	/**
	 * Success handler (returns null)
	 * 
	 * @param hr
	 *            Request response
	 *            
	 * @return T
	 * 
	 * @throws ArangoDb4JException
	 */

	public T success(ArangoDbHttpResponse hr) throws ArangoDb4JException {
		return null;
	}

}
