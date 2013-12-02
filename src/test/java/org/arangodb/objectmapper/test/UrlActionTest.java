package org.arangodb.objectmapper.test;


import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.Version;
import org.arangodb.objectmapper.http.ArangoDbHttpResponse;
import org.arangodb.objectmapper.jackson.JsonSerializer;

public class UrlActionTest extends BaseTestCase {
	
	Database database;
	
	protected void setUp() {
		super.setUp();
		database = new Database(client);
	}

	protected void tearDown() {
		super.tearDown();
	}

	public void test_executeUrlActionGet () {
            try {
                Map<String, Object> m = new HashMap<String, Object>();
                
                ArangoDbHttpResponse response = client.get("/_api/version"); 
                
                assertTrue(response.getContentLength() > 0);

                InputStream is = response.getContentAsStream();
                JsonSerializer js = new JsonSerializer();
                
                HashMap h = js.toObject(is, HashMap.class);

                assertEquals("arango", h.get("server"));
                
            } catch (ArangoDb4JException ex) {
                Logger.getLogger(UrlActionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
                
	}
	
        public void test_executeUrlActionHead () {
            try {
                Map<String, Object> m = new HashMap<String, Object>();
                
                ArangoDbHttpResponse response = client.head("/_api/version"); 

                assertEquals(0, response.getContentLength());
            } catch (ArangoDb4JException ex) {
                Logger.getLogger(UrlActionTest.class.getName()).log(Level.SEVERE, null, ex);
            }
                
	}

	
}
