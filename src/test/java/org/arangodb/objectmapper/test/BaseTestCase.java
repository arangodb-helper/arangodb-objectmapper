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
     * path for the collection api
     */

	protected final static String COLLECTION_PATH = "/_api/collection/"; 
	
	
	protected void setUp() {
		
		try {
			client =  new ArangoDbHttpClient.Builder().build();
		} catch (ArangoDb4JException e1) {
			e1.printStackTrace();
		}

		/*		
		client =  new ArangoDbHttpClient.Builder()
				.host(host)
				.port(port)
				.maxConnections(maxConnections)
				.connectionTimeout(connectionTimeout)
				.socketTimeout(socketTimeout)
				.username(username)
				.password(password)
				.cleanupIdleConnections(cleanupIdleConnections)
				.enableSSL(enableSSL)
				.relaxedSSLSettings(relaxedSSLSettings)
				.sslSocketFactory(sslSocketFactory)
				.url(url)
				.build();
        */
		
		database = new Database(client);

		// delete some collections
		try {
			client.delete(COLLECTION_PATH + Database.getCollectionName(Point.class));
		} catch (ArangoDb4JException e1) {
		}
				
	}

	protected void tearDown() {
		try {
			client.delete(COLLECTION_PATH + Database.getCollectionName(Point.class));
		} catch (ArangoDb4JException e1) {
		}
	}


}
