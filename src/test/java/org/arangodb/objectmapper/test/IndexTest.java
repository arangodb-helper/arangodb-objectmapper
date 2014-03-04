package org.arangodb.objectmapper.test;

import java.util.ArrayList;
import java.util.List;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.Index;


public class IndexTest extends BaseTestCase {
	
	protected void setUp() {
		super.setUp();
		
                try {
			client.delete(database.buildPath(COLLECTION_PATH + docCollection));
		} catch (ArangoDb4JException e1) {
		}		
		
		try {
			database.createCollection(docCollection);
		}
		catch (Exception e) {
			assertTrue(false);
		}
		
	}

	protected void tearDown() {
		try {
			client.delete(database.buildPath(COLLECTION_PATH + docCollection));
		} catch (ArangoDb4JException e1) {
		}		
		
                super.tearDown();
	}

	
	private int countIndexes() {
		// read primary index
		List<Index> listPrimary = null;
		try {
			listPrimary = database.readIndexList(docCollection);
		}
		catch (Exception e) {
			return -1;
		}		
		
		return listPrimary.size();		
	}
	
	
	public void test_getIndexList () {
		// count primary indexes
		assertTrue(countIndexes() > 0);
	}


	public void test_createIndex () {
		int countPrimary = countIndexes();

		List<String> indexFields = new ArrayList<String>();
		indexFields.add("key1");
		indexFields.add("key2");
		
		Index index = new Index();
		index.setType(Index.TYPE_SKIPLIST);
		index.setFields(indexFields);
		
		try {
			database.createIndex(docCollection, index);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}
		
		assertNotNull(index.getId());
		assertEquals(countPrimary + 1, countIndexes());		
	}

	public void test_getPrimaryIndex () {
		Index index = null;
		
		try {
			index = database.readIndex(docCollection + "/0");
		} catch (ArangoDb4JException e) {
		}
		
		assertNotNull(index);
		assertEquals(index.getType(), Index.TYPE_PRIMARY);				
	}

	public void test_deleteIndex () {
		int countPrimary = countIndexes();
		List<String> indexFields = new ArrayList<String>();
		indexFields.add("key1");
		
		Index index = new Index();
		index.setType(Index.TYPE_SKIPLIST);
		index.setFields(indexFields);
		
		try {
			database.createIndex(docCollection, index);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}
		
		assertNotNull(index.getId());
		assertEquals(countPrimary+1, countIndexes());
		
		String id = index.getId();

		try {
			database.deleteIndex(id);
		} catch (ArangoDb4JException e) {
		}
		
		assertEquals(countPrimary, countIndexes());
	}
	
}
