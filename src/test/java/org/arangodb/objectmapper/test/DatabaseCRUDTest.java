package org.arangodb.objectmapper.test;


import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.Version;

public class DatabaseCRUDTest extends BaseTestCase {
	
	Database database;
	
	protected void setUp() {
		super.setUp();
		database = new Database(client, "_system");
		
                try {
			database.deleteCollection(Point.class);
		}
		catch (Exception e) {
		}
                
                // re-create collection
                try {
                        database.createCollection(Point.class);
                }
                catch (Exception e) {
                        // ignore any errors
                }
	}

	protected void tearDown() {
		try {
			database.deleteCollection(Point.class);
		}
		catch (Exception e) {
		}
		
                super.tearDown();
	}

	private Point createPoint(Integer x, Integer y) {
		Point p = new Point(x, y);
		
		try {
			database.createDocument(p);
			assertNotNull(p.getId());
			assertNotNull(p.getKey());
			assertNotNull(p.getRevision());
			assertEquals(x, p.getX());
			assertEquals(y, p.getY());
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
			assertTrue(false);
		}		
		
		return p;
	}
	
	
	public void test_getDatabaseVersion () {
		
		Version version = null;
		
		try {
			version = database.getVersion();
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}
		
		assertNotNull(version);
		assertEquals("arango", version.getServer());
	}

	public void test_createDocument () {		
		createPoint(5, 10);
	}

	public void test_createDocument2times () {
		Point p = createPoint(6, 11);

		try {
			// error
			database.createDocument(p);
			assertTrue(false);
		} catch (ArangoDb4JException e) {
		}
	}
	
	public void test_readDocument () {
		// create
		Point p = createPoint(7, 12);
		String key = p.getKey();
		
		// read
		Point p2 = null;		
		try {
			p2 = database.readDocument(Point.class, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotNull(p2);
		assertEquals(p.getKey(),      p2.getKey());
		assertEquals(p.getId(),       p2.getId());
		assertEquals(p.getRevision(), p2.getRevision());
		assertEquals(p.getX(),        p2.getX());
		assertEquals(p.getY(),        p2.getY());		
	}

	
	public void test_updateDocument () {
		// create
		Point p = createPoint(8, 14);
		String key = p.getKey();
		
		// read
		Point p2 = null;		
		try {
			p2 = database.readDocument(Point.class, key);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		assertNotNull(p2);
		
		// update
		p.setX(9);
		try {
			database.updateDocument(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		
		assertEquals(p.getKey(), p2.getKey());
		assertEquals(p.getId(), p2.getId());
		assertFalse(p.getRevision().equals(p2.getRevision()));
		assertEquals(p.getX(), new Integer(9));
		assertEquals(p.getY(), new Integer(14));		
	}

	public void test_updateDocument2 () {
		// create
		Point p = createPoint(8, 14);
		String key = p.getKey();
                
                final String rev = p.getRevision();
		
		// read
		Point p2 = null;		
		try {
			p2 = database.readDocument(Point.class, key);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		assertNotNull(p2);

		
		// update
		p.setX(9);
		try {
			database.updateDocument(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		

                // read again from database
                p2 = null;
		try {
			p2 = database.readDocument(Point.class, key);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		assertNotNull(p2);
		
		assertEquals(p.getKey(), p2.getKey());
		assertEquals(p.getId(), p2.getId());
		assertFalse(rev.equals(p2.getRevision()));
		assertEquals(p.getX(), new Integer(9));
		assertEquals(p.getY(), new Integer(14));
	}

	public void test_replaceDocument () {
		// create
		Point p = createPoint(8, 14);
		String key = p.getKey();
		
		// read
		Point p2 = null;		
		try {
			p2 = database.readDocument(Point.class, key);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		assertNotNull(p2);
		
		// update
		p.setX(9);
		try {
			database.replaceDocument(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		
		assertEquals(p.getKey(), p2.getKey());
		assertEquals(p.getId(), p2.getId());
		assertFalse(p.getRevision().equals(p2.getRevision()));
		assertEquals(p.getX(), new Integer(9));
		assertEquals(p.getY(), p2.getY());		
	}
	
	public void test_deleteDocument () {
		// create
		Point p = createPoint(8, 14);
		String key = p.getKey();

		// delete
		try {
			database.deleteDocument(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		// read
		Point p2 = null;		
		try {
			// error
			p2 = database.readDocument(Point.class, key);
		} catch (ArangoDb4JException e) {
		}
		
		assertNull(p2);
	}

	public void test_deleteDocument2times () {
		// create
		Point p = createPoint(8, 14);

		// delete
		try {
			database.deleteDocument(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		// delete
		try {
			// error
			database.deleteDocument(p);
			assertTrue(false);
		} catch (ArangoDb4JException e) {
		}
	}
	
}
