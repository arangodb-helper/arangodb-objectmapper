package org.arangodb.objectmapper.test;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.http.ArangoDbHttpClient;

import junit.framework.*;

public abstract class BaseTestCase extends TestCase {

	/**
	 * the ArangoDB connection
	 */

	protected Database database;

	/**
	 * the ArangoDB client
	 */

	protected ArangoDbHttpClient client;

	/**
	 * name of the test vertex collection
	 */

	protected final String docCollection = "unittest_collection";

	/**
	 * name of the test database
	 */

	protected final String dbName = "ArangoDbObjectMapper";

	/**
	 * path for the collection api
	 */

	protected final static String COLLECTION_PATH = "/_api/collection/";

	/**
	 * path for the database api
	 */

	protected final static String DATABASE_PATH = "/_api/database/";

	protected void setUp() {
		
		try {
			
			ArangoDbHttpClient.Builder builder = new ArangoDbHttpClient.Builder();
			client = builder.username("root").password("root").build();
			
		} catch (ArangoDb4JException e1) {
			e1.printStackTrace();
		}

		/*
		 * client = new ArangoDbHttpClient.Builder() .host(host) .port(port) .maxConnections(maxConnections)
		 * .connectionTimeout(connectionTimeout) .socketTimeout(socketTimeout) .username(username) .password(password)
		 * .cleanupIdleConnections(cleanupIdleConnections) .enableSSL(enableSSL) .relaxedSSLSettings(relaxedSSLSettings)
		 * .sslSocketFactory(sslSocketFactory) .url(url) .build();
		 */

		try {
			database = Database.createDatabase(client, dbName);
		} catch (ArangoDb4JException e) {
			if (e.getStatusCode() == 409) {
				// conflict
				database = new Database(client, dbName);
			}
		}

		assertNotNull(database);

		// delete some collections
		try {
			client.delete(database.buildPath(COLLECTION_PATH + Database.getCollectionName(Point.class)));
		} catch (ArangoDb4JException e1) {
		}

	}

	protected void tearDown() {
		try {
			client.delete(database.buildPath(COLLECTION_PATH + Database.getCollectionName(Point.class)));
		} catch (ArangoDb4JException e1) {
		}

		database = null;
	}

}
