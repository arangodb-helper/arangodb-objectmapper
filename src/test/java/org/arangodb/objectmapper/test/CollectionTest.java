package org.arangodb.objectmapper.test;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Collection;
import org.arangodb.objectmapper.ServerRole;

import java.util.List;
import java.util.ArrayList;

public class CollectionTest extends BaseTestCase {
	
	protected void setUp() {
		super.setUp();
		
		try {
			client.delete(database.buildPath(COLLECTION_PATH + docCollection));
		} catch (ArangoDb4JException e1) {
		}		
	}

	protected void tearDown() {
		try {
			client.delete(database.buildPath(COLLECTION_PATH + docCollection));
		} catch (ArangoDb4JException e1) {
		}		
		
                super.tearDown();
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
	
        public void test_createCollectionWithProperties () {
		Collection coll = new Collection(docCollection);
                coll.setWaitForSync(true);
                coll.setJournalSize(1048576);

                assertTrue(coll.getWaitForSync());
		assertEquals(Collection.collType.DOCUMENT, coll.getType());
		assertEquals((Integer) 1048576, coll.getJournalSize());
		assertEquals(docCollection, coll.getName());
	
                Collection collection = null;

                try {	
  		    collection = database.createCollection(coll);
                }
                catch (Exception e) {
                }

                assertNotNull(collection);
                assertNotNull(collection.getId());	
		assertEquals(coll.getWaitForSync(), collection.getWaitForSync());
		assertEquals(coll.getType(), collection.getType());
		assertEquals(coll.getJournalSize(), collection.getJournalSize());
		assertEquals(coll.getName(), collection.getName());
	}
        
        public void test_createCollectionDefaultProperties () {
		Collection collection = null;
                try {
                    collection = database.createCollection(docCollection);
                }
                catch (Exception e) {
                }
	
                assertNotNull(collection);	
                assertNotNull(collection.getId());	
		assertFalse(collection.getWaitForSync());
		assertEquals(Collection.collType.DOCUMENT, collection.getType());
		assertEquals(docCollection, collection.getName());
		assertFalse(collection.getIsVolatile());
		assertFalse(collection.getIsSystem());
	}
        
        public void test_createCollectionWithNumberOfShards () {
                if (database.getServerRole() != ServerRole.roleEnum.COORDINATOR) {
                    return;
                }
		
                Collection coll = new Collection(docCollection);
                coll.setNumberOfShards(4);

		assertEquals((Integer) 4, coll.getNumberOfShards());
	
                Collection collection = null;
                try {	
		    collection = database.createCollection(coll);
                }
                catch (Exception e) {
                }
		
                assertNotNull(collection);	
                assertNotNull(collection.getId());	
		assertEquals((Integer) 4, collection.getNumberOfShards());
		
                try {
			collection = database.readCollection(docCollection);
                        assertEquals((Integer) 4, collection.getNumberOfShards());
		}
		catch (Exception e) {
                        assertFalse(true);
			
		}		
	}
        
        public void test_createCollectionWithShardKey () {
                if (database.getServerRole() != ServerRole.roleEnum.COORDINATOR) {
                    return;
                }
	
                List<String> shardKeys = new ArrayList<String>();
                shardKeys.add("foobar");

                Collection coll = new Collection(docCollection);
                coll.setShardKeys(shardKeys);

                Collection collection = null;
                try {	
		    collection = database.createCollection(coll);
                }
                catch (Exception e) {
                }
		
                assertNotNull(collection);	
                assertNotNull(collection.getId());	
		assertEquals(shardKeys, collection.getShardKeys());
		
                try {
			collection = database.readCollection(docCollection);
		        assertEquals(shardKeys, collection.getShardKeys());
		}
		catch (Exception e) {
                        assertFalse(true);
			
		}		
	}
        
        public void test_createCollectionWithShardKeys () {
                if (database.getServerRole() != ServerRole.roleEnum.COORDINATOR) {
                    return;
                }
	
                List<String> shardKeys = new ArrayList<String>();
                shardKeys.add("_key");
                shardKeys.add("a");
                shardKeys.add("b");

                Collection coll = new Collection(docCollection);
                coll.setShardKeys(shardKeys);

                Collection collection = null;
                try {	
		    collection = database.createCollection(coll);
                }
                catch (Exception e) {
                }
		
                assertNotNull(collection);	
                assertNotNull(collection.getId());	
		assertEquals(shardKeys, collection.getShardKeys());
		
                try {
			collection = database.readCollection(docCollection);
		        assertEquals(shardKeys, collection.getShardKeys());
		}
		catch (Exception e) {
                        assertFalse(true);
			
		}		
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
		catch (ArangoDb4JException e) {
                        // expecting conflict
                        assertEquals((Integer) 409, e.getStatusCode());
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

	public void test_deleteCollection () {
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
