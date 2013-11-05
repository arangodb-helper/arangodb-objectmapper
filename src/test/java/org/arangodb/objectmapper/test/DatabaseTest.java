package org.arangodb.objectmapper.test;


import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.Version;

public class DatabaseTest extends BaseTestCase {
	
	protected void setUp() {
		super.setUp();
	}

	protected void tearDown() {
		super.tearDown();
	}

	public void test_buildPathNoName () {
		Database database = new Database(client);

                assertEquals("/foo", database.buildPath("/foo"));
                assertEquals("/bar/baz", database.buildPath("/bar/baz"));
                assertEquals("/bar/baz?a=1&b=2", database.buildPath("/bar/baz?a=1&b=2"));
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
	
}
