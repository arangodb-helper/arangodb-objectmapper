package org.arangodb.objectmapper.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.Version;
import org.arangodb.objectmapper.http.ArangoDbHttpResponse;
import org.arangodb.objectmapper.jackson.JsonSerializer;

public class DatabaseTest extends BaseTestCase {
	
	protected void setUp() {
		super.setUp();
	}

	protected void tearDown() {
		super.tearDown();
	}

	public void test_buildPathWithName () {
		Database database = new Database(client, "_system");

                assertEquals("/_db/_system/foo", database.buildPath("/foo"));
                assertEquals("/_db/_system/bar/baz", database.buildPath("/bar/baz"));
                assertEquals("/_db/_system/bar/baz?a=1&b=2", database.buildPath("/bar/baz?a=1&b=2"));
	}
	
        public void test_buildPathWithName2 () {
		Database database = new Database(client, "mydb");

                assertEquals("/_db/mydb/foo", database.buildPath("/foo"));
                assertEquals("/_db/mydb/bar/baz", database.buildPath("/bar/baz"));
                assertEquals("/_db/mydb/bar/baz?a=1&b=2", database.buildPath("/bar/baz?a=1&b=2"));
	}

        public void test_createAndDropDatabase() {
                try { 
			database.deleteDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err1) {
                }

                Database db = null;
                try { 
			db = database.createDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err2) {
                }

                assertNotNull(db);
                
                try {
                    ArangoDbHttpResponse response = client.get("/_db/ArangoDbObjectMapperTestDb/_api/database/current"); 
                    assertTrue(response.getContentLength() > 0);

                    InputStream is = response.getContentAsStream();
                    JsonSerializer js = new JsonSerializer();
                 
                    HashMap h = js.toObject(is, HashMap.class);
                    assertEquals("ArangoDbObjectMapperTestDb", ((Map) h.get("result")).get("name"));
                }
                catch (Exception e) {
                    assertFalse(true);
                }

                try {
             		database.deleteDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err4) {
                }
        }
        
        public void test_createDatabase_2times() {
                try { 
			database.deleteDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err1) {
                }

                Database db = null;
                try { 
			db = database.createDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err2) {
                }

                assertNotNull(db);
                try { 
			db = database.createDatabase("ArangoDbObjectMapperTestDb");
                        assertTrue(false);
                }
                catch (ArangoDb4JException err3) {
                        assertEquals((Integer) 409, err3.getStatusCode());
                }

                try { 
			database.deleteDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err4) {
                }
        }
        
        public void test_dropNonExistingDatabase() {
                try { 
			database.deleteDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err1) {
                }

                try { 
			database.deleteDatabase("ArangoDbObjectMapperTestDb");
                }
                catch (ArangoDb4JException err2) {
                        assertEquals((Integer) 404, err2.getStatusCode());
                }
        }
	
}
