package org.arangodb.objectmapper.test;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.ArangoDbQuery;
import org.arangodb.objectmapper.Cursor;
import org.arangodb.objectmapper.PropertyFilter;

public class QueryTest extends BaseTestCase {
	
	protected void setUp() {
		super.setUp();
		
		try {
			database.deleteCollection(Point.class);
		}
		catch (Exception e) {
			
		}
	}

	protected void tearDown() {
		super.tearDown();
		
		try {
			database.deleteCollection(Point.class);
		}
		catch (Exception e) {
			
		}
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
	
	private void createNumPoints (int num) {
		for (int i = 0; i < num; ++i) {
			createPoint(5 + i, 10 + i); 						
		}
	}
	
	
	public void test_getAllByQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class); 
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
	}

	public void test_getAllByQueryWithLimit () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.limit(5);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(5, count);
	}
	
        public void test_getAllByQueryWithBatchSize () {
		int num = 1000;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.batchSize(50);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
	}

	public void test_databaseGetAll () {
		int num = 10;
		createNumPoints(num);

		Cursor<Point> cursor = null;
		try {
			cursor = database.getAll(Point.class);
		} catch (ArangoDb4JException e) {
		}
		
		assertNotNull(cursor);
		
		int count = 0;
		while (cursor.hasNext()) {
			cursor.next();
			++count;
		}
		
		assertEquals(num, count);
	}

	public void test_hasQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.has("x", 7);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(1, count);
	}
	
	public void test_compareQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.has("y", 15, PropertyFilter.Compare.GREATER_THAN);
		query.has("y", 17, PropertyFilter.Compare.LESS_THAN_EQUAL);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(2, count);
	}

	public void test_intervalQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.interval("y", 16, 18);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(2, count);
	}

	public void test_createAndQueryMany () {
		int num = 10000;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
                query.batchSize(1000);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
	}
	
}
