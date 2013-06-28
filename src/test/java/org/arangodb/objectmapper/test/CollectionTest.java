package org.arangodb.objectmapper.test;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Collection;


public class CollectionTest extends BaseTestCase {
	
	protected void setUp() {
		super.setUp();
		
		try {
			client.delete(COLLECTION_PATH + docCollection);
		} catch (ArangoDb4JException e1) {
		}		
	}

	protected void tearDown() {
		super.tearDown();
		
		try {
			client.delete(COLLECTION_PATH + docCollection);
		} catch (ArangoDb4JException e1) {
		}		
	}

	public void test_createCollection () {
		Collection collection = null;
		
		try {
			collection = database.createCollection(docCollection);
		}
		catch (Exception e) {
			
		}
		
		assertNotNull(collection);				
	}

	public void test_createCollection2times () {
		Collection collection = null;
		
		try {
			collection = database.createCollection(docCollection);
		}
		catch (Exception e) {
			
		}
		
		assertNotNull(collection);	
		
		Collection collection2 = null;
		
		try {
			// error
			collection2 = database.createCollection(docCollection);
		}
		catch (Exception e) {
			
		}
		
		assertNull(collection2);				
	}

	public void test_readCollection () {
		Collection collection = new Collection(docCollection);
		
		try {
			collection.setWaitForSync(false);
			collection.setType(Collection.collType.EDGE);
			collection = database.createCollection(collection);
		}
		catch (Exception e) {
			
		}
		
		assertNotNull(collection);	
		
		Collection collection2 = null;
		
		try {
			collection2 = database.readCollection(docCollection);
		}
		catch (Exception e) {
			
		}		
		assertNotNull(collection2);	
		assertNotNull(collection2.getId());	
		assertEquals(collection2.getWaitForSync(), collection.getWaitForSync());
		assertEquals(collection2.getType(), collection.getType());
	}

	public void test_delteCollection () {
		Collection collection = null;
		
		try {
			collection = database.createCollection(docCollection);
		}
		catch (Exception e) {
			
		}		
		assertNotNull(collection);	
		
		
		// delete
		try {
			database.deleteCollection(docCollection);
		}
		catch (Exception e) {
			assertTrue(false);
		}

		// check
		Collection collection2 = null;
		
		try {
			collection2 = database.readCollection(docCollection);
		}
		catch (Exception e) {
			
		}		
		assertNull(collection2);	
	}
	
}
