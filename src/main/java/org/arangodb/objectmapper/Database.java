package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.arangodb.objectmapper.http.ArangoDbHttpClient;
import org.arangodb.objectmapper.http.ArangoDbHttpResponse;
import org.arangodb.objectmapper.http.ResponseCallback;
import org.arangodb.objectmapper.http.RestHandler;
import org.arangodb.objectmapper.http.RevisionResponse;
import org.arangodb.objectmapper.jackson.ArangoDbDocument;
import org.arangodb.objectmapper.jackson.JsonSerializer;
import org.arangodb.objectmapper.util.ArangoDbAssert;

import com.fasterxml.jackson.databind.JsonNode;

public class Database {

	/**
	 * rest handler
	 */

	private final RestHandler rest;

	/**
	 * the json serializer (jackson)
	 */

	private final JsonSerializer serializer;
        
        /**
	 * Database name
	 */

        private String name;
	
        /**
	 * Database prefix
	 */

	public final static String DB_PREFIX = "/_db/";

	/**
	 * Path to document api
	 */

	public final static String DOCUMENT_PATH = "/_api/document/";
	
	/**
	 * Path to version api
	 */

	public final static String VERSION_PATH = "/_api/version";

	/**
	 * Path to collection api
	 */

	public final static String COLLECTION_PATH = "/_api/collection";

	/**
	 * Path to index api
	 */

	public final static String INDEX_PATH = "/_api/index";

	/**
	 * Create database
	 * 
	 * @param client
	 *            a http client connection
         *
	 * @param name
	 *            database name
	 */

	public Database(ArangoDbHttpClient client, String name) {
		this.rest = new RestHandler(client);
		this.serializer = new JsonSerializer();
                this.name = name;
	}

	/**
	 * Create database
	 * 
	 * @param client
	 *            a http client connection
	 */

	public Database(ArangoDbHttpClient client) {
                this(client, "");
	}
	
        /**
	 * Build a database-specific path
	 * 
	 * @param relPath the path without the database name
         *
	 * @return String The database-specific path
	 */

        public String buildPath(String relPath) {
                if (this.name == "") {
                        return relPath;
                }

                return DB_PREFIX + this.name + relPath;
        }

	/**
	 * Request the version of ArangoDB
	 * 
	 * @return Version The version object number
	 * 
	 * @throws ArangoDb4JException
	 *             on
	 */

	public Version getVersion() throws ArangoDb4JException {
		return rest.get(buildPath(VERSION_PATH), new ResponseCallback<Version>() {
			@Override
			public Version success(ArangoDbHttpResponse hr)
					throws ArangoDb4JException {
				return serializer.toObject(hr.getContentAsStream(),
						Version.class);
			}
		});
	}

	// /////////////////////////////////////////////////////////////////////////
	// CRUD functions
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Save object to ArangoDB
	 * 
	 * @param o the object to save
	 * 
	 * @return ArangoDbDocument Returns the given object. (Id, Key and Revision
	 *         is filled)
	 * 
	 * @throws ArangoDb4JException
	 *             on
	 */

	public <T extends ArangoDbDocument> T createDocument(T o)
			throws ArangoDb4JException {
		ArangoDbAssert.notNull(o, "given object is null");
		
		if (!o.isNew()) {
			throw new ArangoDb4JException("Document is not new.");
		}

		String collection = getCollectionName(o.getClass());
		String path = DOCUMENT_PATH + "?createCollection=true&collection=" + encodePart(collection);

		RevisionResponse resp = rest.post(buildPath(path), serializer.toJson(o),
				new ResponseCallback<RevisionResponse>() {
					@Override
					public RevisionResponse success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toObject(hr.getContentAsStream(),
								RevisionResponse.class);
					}
				});

		o.setId(resp.getId());
		o.setKey(resp.getKey());
		o.setRevision(resp.getRevision());

		return o;
	}

	/**
	 * Load object
	 * 
	 * @param c A class
	 * @param key The document key
	 * 
	 * @return T The loaded object
	 * 
	 * @throws ArangoDb4JException
	 *             on connection, database and unserialize errors 
	 */

	public <T> T readDocument(final Class<T> c, String key)
			throws ArangoDb4JException {
		ArangoDbAssert.notNull(c, "given class is null");
		ArangoDbAssert.hasText(key, "document key is empty");

		String collection = getCollectionName(c);
		String path = DOCUMENT_PATH + collection + "/" + encodePart(key);

		return rest.get(buildPath(path), new ResponseCallback<T>() {
			@Override
			public T success(ArangoDbHttpResponse hr)
					throws ArangoDb4JException {
				return serializer.toObject(hr.getContentAsStream(), c);
			}
		});
	}
	
        /**
	 * Update object 
	 * 
	 * @param o     the object to update
	 *
	 * @return T    Returns the given object. (new Revision
	 *         is filled)
	 * 
	 * @throws ArangoDb4JException
	 *             on connection, database and unserialize errors 
	 */

	public <T extends ArangoDbDocument> T updateDocument(T o)
			throws ArangoDb4JException {
		return updateDocument(o, true);
        }
	
	
	/**
	 * Update object 
	 * 
	 * @param o     the object to update
	 *
         * @param keepNull     whether or not null values remove attributes
	 * 
	 * @return T    Returns the given object. (new Revision
	 *         is filled)
	 * 
	 * @throws ArangoDb4JException
	 *             on connection, database and unserialize errors 
	 */

	public <T extends ArangoDbDocument> T updateDocument(T o, boolean keepNull)
			throws ArangoDb4JException {
		ArangoDbAssert.notNull(o, "given object is null");
		
		if (o.isNew()) {
			throw new ArangoDb4JException("Document is new.");
		}

		String collection = getCollectionName(o.getClass());
		String path = DOCUMENT_PATH + collection + "/" + encodePart(o.getKey()) +
                              "?keepNull=" + (keepNull ? "true" : "false");

		// TODO: add revision check
		
		RevisionResponse resp = rest.patch(buildPath(path), serializer.toJson(o),
				new ResponseCallback<RevisionResponse>() {
					@Override
					public RevisionResponse success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toObject(hr.getContentAsStream(),
								RevisionResponse.class);
					}
				});

		o.setRevision(resp.getRevision());

		return o;
	}

	/**
	 * Replace object 
	 * 
	 * @param o     the object to replace
	 * 
	 * @return T    Returns the given object. (new Revision
	 *         is filled)
	 * 
	 * @throws ArangoDb4JException
	 *             on connection, database and unserialize errors 
	 */

	public <T extends ArangoDbDocument> T replaceDocument(T o)
			throws ArangoDb4JException {
		ArangoDbAssert.notNull(o, "given object is null");
		
		if (o.isNew()) {
			throw new ArangoDb4JException("Document is new.");
		}

		String collection = getCollectionName(o.getClass());
		String path = DOCUMENT_PATH + collection + "/" + encodePart(o.getKey());

		// TODO: add revision check
		
		RevisionResponse resp = rest.put(buildPath(path), serializer.toJson(o),
				new ResponseCallback<RevisionResponse>() {
					@Override
					public RevisionResponse success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toObject(hr.getContentAsStream(),
								RevisionResponse.class);
					}
				});

		o.setRevision(resp.getRevision());

		return o;
	}

	/**
	 * Delete object 
	 * 
	 * @param o the object to delete
	 * 
	 * @throws ArangoDb4JException
	 *             on connection, database and unserialize errors 
	 */

	public void deleteDocument(ArangoDbDocument o)
			throws ArangoDb4JException  {
		ArangoDbAssert.notNull(o, "given object is null");
		
		if (o.isNew()) {
			// nothing to do
			return;
		}

		String collection = getCollectionName(o.getClass());
		String path = DOCUMENT_PATH + collection + "/" + encodePart(o.getKey());

		// TODO: add revision check
		
		rest.delete(buildPath(path),
				new ResponseCallback<RevisionResponse>() {
					@Override
					public RevisionResponse success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toObject(hr.getContentAsStream(),
								RevisionResponse.class);
					}
				});

	}

	// /////////////////////////////////////////////////////////////////////////
	// cursor and simple queries
	// /////////////////////////////////////////////////////////////////////////
	
	public <T extends ArangoDbDocument> ArangoDbQuery<T> getQuery(final Class<T> c) {
		return new ArangoDbQuery<T>(this, c); 
	}

	public <T extends ArangoDbDocument> Cursor<T> getAll (final Class<T> c) throws ArangoDb4JException {
		return getAll (c, null);
	}
	
	public <T extends ArangoDbDocument> Cursor<T> getAll (final Class<T> c, Long max) throws ArangoDb4JException {
		ArangoDbAssert.notNull(c, "given class is null");
		ArangoDbQuery<T> query = new ArangoDbQuery<T>(this, c); 		
		
		if (null != max) {
			query.limit(max);
		}
		
		return query.execute();
	}

	// /////////////////////////////////////////////////////////////////////////
	// Indices
	// /////////////////////////////////////////////////////////////////////////

	public <T extends ArangoDbDocument> Index createIndex (final Class<T> c, Index index)  throws ArangoDb4JException {
		return createIndex(new Collection(c), index);
	}
	
	public Index createIndex (String name, Index index)  throws ArangoDb4JException {
		return createIndex(new Collection(name), index);
	}
	
	public Index createIndex (Collection coll, Index index)  throws ArangoDb4JException  {
		ArangoDbAssert.notNull(coll, "given collection is null");
		ArangoDbAssert.notNull(index, "given index is null");
		
		String path = INDEX_PATH + "?collection=" + encodePart(coll.getName());
		
		JsonNode root = rest.post(buildPath(path), serializer.toJson(index.getAsMap()),
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toJsonNode(hr.getContentAsStream());
					}
				});
		
		index.setValues(root);
		
		return index;
	}
	
	public Index readIndex (String id)  throws ArangoDb4JException  {
		Index index = new Index();
		index.setId(id);
		return readIndex(index);
	}
	
	public Index readIndex (Index index)  throws ArangoDb4JException {
		ArangoDbAssert.notNull(index, "given collection is null");
		
		String path = INDEX_PATH + "/" + index.getId();
		
		JsonNode root = rest.get(buildPath(path),
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toJsonNode(hr.getContentAsStream());
					}
				});
		
		index.setValues(root);
		
		return index;
	}

	public <T extends ArangoDbDocument> List<Index> readIndexList (final Class<T> c)  throws ArangoDb4JException {
		return readIndexList(new Collection(c));
	}
	
	public List<Index> readIndexList (String name)  throws ArangoDb4JException {
		return readIndexList(new Collection(name));
	}

	public List<Index> readIndexList (Collection coll)  throws ArangoDb4JException {
		ArangoDbAssert.notNull(coll, "given collection is null");
		
		String path = INDEX_PATH + "?collection=" + encodePart(coll.getName());
		
		JsonNode root = rest.get(buildPath(path),
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toJsonNode(hr.getContentAsStream());
					}
				});
		
		return Index.createIndexList(root);
	}
	
	public <T extends ArangoDbDocument> void deleteIndex (String id)  throws ArangoDb4JException {
		Index index = new Index();
		index.setId(id);
		deleteIndex(index);
	}
	
	public void deleteIndex (Index index)  throws ArangoDb4JException { 
		ArangoDbAssert.notNull(index, "given index is null");
		String path = INDEX_PATH + "/" + index.getId();	
		rest.delete(buildPath(path));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// Collections
	// /////////////////////////////////////////////////////////////////////////

	public <T extends ArangoDbDocument> Collection createCollection (final Class<T> c)  throws ArangoDb4JException {
		return createCollection(new Collection(c));
	}
	
	public Collection createCollection (String name)  throws ArangoDb4JException {
		return createCollection(new Collection(name));
	}
	
	public Collection createCollection (Collection coll)  throws ArangoDb4JException {
		ArangoDbAssert.notNull(coll, "given collection is null");
		
		rest.post(buildPath(COLLECTION_PATH), serializer.toJson(coll.getAsMap()),
				new ResponseCallback<Integer>() {
					@Override
					public Integer success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return null;
					}
				});

		String path = COLLECTION_PATH + "/" + encodePart(coll.getName()) + "/properties";
		
		JsonNode root = rest.get(buildPath(path),
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toJsonNode(hr.getContentAsStream());
					}
				});
		
		coll.setValues(root);
		
		return coll;
	}
	
	public <T extends ArangoDbDocument> Collection readCollection (final Class<T> c)  throws ArangoDb4JException {
		return readCollection(new Collection(c));
	}
	
	public Collection readCollection (String name)  throws ArangoDb4JException {
		return readCollection(new Collection(name));
	}
	
	public Collection readCollection (Collection coll)  throws ArangoDb4JException {
		ArangoDbAssert.notNull(coll, "given collection is null");
		
		String path = COLLECTION_PATH + "/" + encodePart(coll.getName()) + "/properties";
		
		JsonNode root = rest.get(buildPath(path),
				new ResponseCallback<JsonNode>() {
					@Override
					public JsonNode success(ArangoDbHttpResponse hr)
							throws ArangoDb4JException {
						return serializer.toJsonNode(hr.getContentAsStream());
					}
				});
		
		coll.setValues(root);
		
		return coll;
	}

	public <T extends ArangoDbDocument> void deleteCollection (final Class<T> c)  throws ArangoDb4JException {
		deleteCollection(new Collection(c));
	}
	
	public void deleteCollection (String name)  throws ArangoDb4JException {
		deleteCollection(new Collection(name));
	}

	public void deleteCollection (Collection coll)  throws ArangoDb4JException {
		ArangoDbAssert.notNull(coll, "given collection is null");
		String path = COLLECTION_PATH + "/" + encodePart(coll.getName());	
		rest.delete(buildPath(path));
	}
				
        private String encodePart (String value) throws ArangoDb4JException {
                try {
                    return URLEncoder.encode(value, "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    throw new ArangoDb4JException("unexpected failure in encodePart");
                }
        }
	
	// /////////////////////////////////////////////////////////////////////////
	// Utilities
	// /////////////////////////////////////////////////////////////////////////
	
	public static <T> String getCollectionName (final Class<T> c) {
		return c.getName().replace('.', '-');
	}
	
	public JsonSerializer getSerializer() {
		return serializer;
	}
	
	public RestHandler getRestHandler() {
		return rest;
	}
	
}
