package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arangodb.objectmapper.jackson.ArangoDbDocument;

import com.fasterxml.jackson.databind.JsonNode;

public final class Collection {

	/**
	 * states
	 */

	public static enum collState {
	UNLOADED, LOADED, UNLOADING, DELETED, LOADING, UNKNOWN
	}

	/**
	 * types
	 */

	public static enum collType {
		EDGE, DOCUMENT, UNKNOWN
	}

	/**
	 * The id of the collection
	 */

	private String id;

	/**
	 * The name of the collection.
	 */

	private String name;

	/**
	 * The state of the collection
	 */

	private collState state;

	/**
	 * The type of the collection
	 */

	private collType type;

	/**
	 * (optional, default: false): If true then the data is synchronised to disk before returning from a create or
	 * update of an document.
	 */

	private Boolean waitForSync;

	/**
	 * (optional, default is a configuration parameter): The maximal size of a journal or datafile. Note that this also
	 * limits the maximal size of a single object. Must be at least 1MB.
	 */

	private Integer journalSize;

	/**
	 * (optional, default is false): If true, create a system collection. In this case collection-name should start with
	 * an underscore. End users should normally create non-system collections only. API implementors may be required to
	 * create system collections in very special occasions, but normally a regular collection will do.
	 */

	private Boolean isSystem;

	/**
	 * (optional, default is false): If true then the collection data is kept in-memory only and not made persistent.
	 * Unloading the collection will cause the collection data to be discarded. Stopping or re-starting the server will
	 * also cause full loss of data in the collection. Setting this option will make the resulting collection be
	 * slightly faster than regular collections because ArangoDB does not enforce any synchronisation to disk and does
	 * not calculate any CRC checksums for datafiles (as there are no datafiles).
	 */

	private Boolean isVolatile;

	/**
	 * (optional, default is 1): The number of shards of the collection. The option is not used in a single-server
	 * setup.
	 */

	private Integer numberOfShards;

	/**
	 * (optional): The shard key attributes of the collection. If empty, this defaults to the "_key" attribute in a
	 * cluster setup. The option is not used in a single-server setup.
	 */

	private List<String> shardKeys;

	public <T extends ArangoDbDocument> Collection(final Class<T> c) {
		this(Database.getCollectionName(c));
	}

	public Collection(String name) {
		this.name = name;
		this.state = collState.UNKNOWN;
		this.type = collType.DOCUMENT;
	}

	public void setValues(JsonNode root) {
		id = root.has("id") ? root.get("id").asText() : null;
		name = root.has("name") ? root.get("name").asText() : null;

		if (root.has("state")) {
			switch (root.get("state").asInt()) {
			case 2:
				this.state = collState.UNLOADED;
				break;
			case 3:
				this.state = collState.LOADED;
				break;
			case 4:
				this.state = collState.UNLOADING;
				break;
			case 5:
				this.state = collState.DELETED;
				break;
			case 6:
				this.state = collState.LOADING;
				break;
			default:
				this.state = collState.UNKNOWN;
			}
		} else {
			this.state = collState.UNKNOWN;
		}

		if (root.has("type")) {
			switch (root.get("type").asInt()) {
			case 2:
				this.type = collType.DOCUMENT;
				break;
			case 3:
				this.type = collType.EDGE;
				break;
			default:
				this.type = collType.UNKNOWN;
			}
		} else {
			this.type = collType.UNKNOWN;
		}

		waitForSync = root.has("waitForSync") ? root.get("waitForSync").asBoolean() : null;
		journalSize = root.has("journalSize") ? root.get("journalSize").asInt() : null;
		isSystem = root.has("isSystem") ? root.get("isSystem").asBoolean() : null;
		isVolatile = root.has("isVolatile") ? root.get("isVolatile").asBoolean() : false;
		numberOfShards = root.has("numberOfShards") ? root.get("numberOfShards").asInt() : null;
	}

	public Map<String, Object> getAsMap() {
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("name", name);
		if (collType.EDGE == type) {
			result.put("type", 3);
		} else {
			result.put("type", 2);
		}

		if (null != waitForSync) {
			result.put("waitForSync", waitForSync);
		}
		if (null != journalSize) {
			result.put("journalSize", journalSize);
		}
		if (null != isSystem) {
			result.put("isSystem", isSystem);
		}
		if (null != isVolatile) {
			result.put("isVolatile", isVolatile);
		}
		if (null != numberOfShards) {
			result.put("numberOfShards", numberOfShards);
		}
		if (null != shardKeys) {
			result.put("shardKeys", shardKeys);
		}

		return result;
	}

	public String getId() {
		return id;
	}

	public collType getType() {
		return type;
	}

	public collState getState() {
		return state;
	}

	public String getName() {
		return name;
	}

	public Boolean getWaitForSync() {
		return waitForSync;
	}

	public void setWaitForSync(Boolean waitForSync) {
		this.waitForSync = waitForSync;
	}

	public Integer getJournalSize() {
		return journalSize;
	}

	public void setJournalSize(Integer journalSize) {
		this.journalSize = journalSize;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	public Boolean getIsVolatile() {
		return isVolatile;
	}

	public void setIsVolatile(Boolean isVolatile) {
		this.isVolatile = isVolatile;
	}

	public Integer getNumberOfShards() {
		return numberOfShards;
	}

	public void setNumberOfShards(Integer numberOfShards) {
		this.numberOfShards = numberOfShards;
	}

	public List<String> getShardKeys() {
		return shardKeys;
	}

	public void setShardKeys(List<String> shardKeys) {
		this.shardKeys = shardKeys;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(collState state) {
		this.state = state;
	}

	public void setType(collType type) {
		this.type = type;
	}

}
