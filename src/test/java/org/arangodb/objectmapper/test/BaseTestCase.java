package org.arangodb.objectmapper.test;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;
import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.http.ArangoDbHttpClient;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {

	private static Logger LOG = Logger.getLogger(BaseTestCase.class);

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

			Properties properties = loadPropertiesFromFile();

			ArangoDbHttpClient.Builder builder = new ArangoDbHttpClient.Builder();

			client = builder.username(properties.getProperty("arangodb.user"))
					.password(properties.getProperty("arangodb.password")).host(properties.getProperty("arangodb.host"))
					.port(Integer.parseInt(properties.getProperty("arangodb.port"))).build();

		} catch (ArangoDb4JException e1) {
			LOG.error("Cant create Builder", e1);
		}

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
			LOG.error("Cant create Builder", e1);
		}

	}

	protected void tearDown() {
		
		try {
			
			client.delete(database.buildPath(COLLECTION_PATH + Database.getCollectionName(Point.class)));
			
		} catch (ArangoDb4JException e1) {
			LOG.error("Cant create Builder", e1);
		}

		database = null;
	}

	private Properties loadPropertiesFromFile() {

		Properties props = new Properties();

		try {

			LOG.info("Try to load Properties from " + BaseTestCase.class.getResource("/arangodb.properties").getFile());
			
			InputStream resourceAsStream = BaseTestCase.class.getResourceAsStream("/arangodb.properties");
			props.load(resourceAsStream);
			
			LOG.info("Load " + props.size() +" Properties from " + BaseTestCase.class.getResource("/arangodb.properties").getFile());
			
			System.getProperties().forEach((key, value) -> {
				LOG.info(key + " - " + value);
			});
			
			props.forEach((key, val) -> {
				
				String systemPropName = key.toString().replace(".", "_");
				LOG.info("Check Env " + systemPropName + " for override of " + key);
				String systemProp = System.getProperty(systemPropName);
				
				
				
				if(systemProp != null) {
					LOG.info("Found System Property for " + key + " with Value " + systemProp);
					props.put(key, systemProp);
				}
				
			});

		} catch (Exception e) {
			LOG.warn("Cant read Properties from File", e);
		}

		return props;

	}

}
