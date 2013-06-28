package org.arangodb.objectmapper.jackson;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.io.InputStream;

import org.arangodb.objectmapper.ArangoDb4JException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonSerializer {

	private final ObjectMapper objectMapper;
	
	public JsonSerializer () {
		this.objectMapper = new ObjectMapper();
		
		// std config:
		this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		this.objectMapper.getSerializationConfig().withSerializationInclusion(JsonInclude.Include.NON_NULL);		
	}
	
	public String toJson(Object o) throws ArangoDb4JException {
		try {
			String json = objectMapper.writeValueAsString(o);
			return json;
		} catch (Exception e) {
			throw new ArangoDb4JException(e);
		}
	}

	public <T> T toObject(InputStream istream,  Class<T> valueType) throws ArangoDb4JException {
		try {
			return objectMapper.readValue(istream, valueType);
		} catch (Exception e) {
			throw new ArangoDb4JException(e);
		}
	}

	public <T> T toObject(JsonNode node,  Class<T> valueType) throws ArangoDb4JException {
		try {
			return objectMapper.convertValue(node, valueType);
		} catch (Exception e) {
			throw new ArangoDb4JException(e);
		}
	}

	public JsonNode toJsonNode(InputStream istream) throws ArangoDb4JException {
		try {
			return objectMapper.readTree(istream);
			
		} catch (Exception e) {
			throw new ArangoDb4JException(e);
		}
	}
}
