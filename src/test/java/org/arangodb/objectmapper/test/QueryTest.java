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
		assertEquals(num, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
		assertEquals(num, iter.count());
	}

	public void test_getAllByQueryWithLimit () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.limit(5);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		assertEquals(5, iter.count());

		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(5, count);
		assertEquals(5, iter.count());

	}
	
        public void test_getAllByQueryWithBatchSize () {
		int num = 200;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.batchSize(50);
		
		int count = 0;
		Cursor<Point> iter = query.execute();
		assertEquals(num, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
		assertEquals(num, iter.count());
	}
        
        public void test_getAllByQueryWithModifiedBatchSize () {
		int num = 200;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.batchSize(100);
		
		int count = 0;
		Cursor<Point> iter = query.execute();
		assertEquals(num, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
		assertEquals(num, iter.count());
	}
        
        public void test_getAllByQueryWithoutCount () {
		int num = 300;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.count(false);
		
		int count = 0;
		Cursor<Point> iter = query.execute();
                // count is not set
		assertEquals(0, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
		assertEquals(0, iter.count());
	}
        
        public void test_getAllByQueryWithoutCountWithLimit () {
		int num = 300;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.count(false);
                query.limit(50);
		
		int count = 0;
		Cursor<Point> iter = query.execute();
                // count is not set
		assertEquals(0, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(50, count);
		assertEquals(0, iter.count());
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
		assertEquals(num, cursor.count());
		
		int count = 0;
		while (cursor.hasNext()) {
			cursor.next();
			++count;
		}
		
		assertEquals(num, count);
		assertEquals(num, cursor.count());
	}

	public void test_hasQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.has("x", 7);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		assertEquals(1, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(1, count);
		assertEquals(1, iter.count());
	}
	
	public void test_compareQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.has("y", 15, PropertyFilter.Compare.GREATER_THAN);
		query.has("y", 17, PropertyFilter.Compare.LESS_THAN_EQUAL);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		assertEquals(2, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(2, count);
		assertEquals(2, iter.count());
	}

	public void test_intervalQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
		query.interval("y", 16, 18);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		assertEquals(2, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(2, count);
		assertEquals(2, iter.count());
	}

	public void test_createAndQueryMany () {
		int num = 300;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = database.getQuery(Point.class);
                query.batchSize(100);
		
		int count = 0;
		Cursor<Point> iter =  query.execute();
		assertEquals(num, iter.count());
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
		assertEquals(num, iter.count());
	}
	
}
