package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Index {

	public final static String TYPE_SKIPLIST = "skiplist"; 
	public final static String TYPE_HASH = "hash"; 
	public final static String TYPE_FULLTEXT = "fulltext"; 
	public final static String TYPE_PRIMARY = "primary"; 
		
	/**
	 * id of the index
	 */

	private String id;

	/**
	 * the index type
	 */

	private String type;
	
	/**
	 * is the index unique
	 */

	private boolean unique;
	
	/**
	 * the fields of the index
	 */

	private List<String> fields;
	
	public Index () {
		this.id = null;
		this.type = TYPE_SKIPLIST;
		this.unique = false;
		this.fields = new ArrayList<String>();
	}

	public void setValues (JsonNode node) {		
		id = node.has("id") ? node.get("id").asText() : null;
		type = node.has("type") ? node.get("type").asText() : null;
		unique = node.has("unique") ? node.get("unique").asBoolean() : null;		
		this.fields = new ArrayList<String>();
		
		if (node.has("fields")) {
			JsonNode tmp = node.get("fields");
			if (tmp.isArray()) {
				ArrayNode array = (ArrayNode) tmp;
				for (int i = 0; i < array.size(); ++i) {
					this.fields.add(array.get(i).asText());										
				}
			}
		}
	}
	
	public Map<String, Object> getAsMap() {
		Map<String, Object> result = new HashMap<String, Object>();		
		result.put("type", type);
		result.put("unique", unique);
		result.put("fields", fields);		
		return result;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public boolean isUnique() {
		return unique;
	}

	public List<String> getFields() {
		return fields;
	}
	
	public static List<Index> createIndexList(JsonNode root) {
		List<Index> result = new ArrayList<Index>();
		
		if (root.has("indexes")) {
			JsonNode node = root.get("indexes");
			if (node.isArray()) {
				for (int i = 0; i < node.size(); ++i) {
					Index index = new Index();
					index.setValues(node.get(i));
					result.add(index);										
				}
			}
		}		
		
		return result;
	}
	
}
