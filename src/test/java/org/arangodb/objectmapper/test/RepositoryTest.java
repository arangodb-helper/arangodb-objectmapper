package org.arangodb.objectmapper.test;

import java.util.ArrayList;
import java.util.Iterator;

import org.arangodb.objectmapper.ArangoDb4JException;
import org.arangodb.objectmapper.ArangoDbQuery;
import org.arangodb.objectmapper.ArangoDbRepository;
import org.arangodb.objectmapper.Cursor;
import org.arangodb.objectmapper.Database;
import org.arangodb.objectmapper.Index;
import org.arangodb.objectmapper.PropertyFilter;

public class RepositoryTest extends BaseTestCase {
	
	private class PointRepository extends ArangoDbRepository<Point> {
		
		public PointRepository(Database database) {
			super(database, Point.class);
		}
		
		protected void createIndexes() {
			Index index = new Index();
			
			index.setType(Index.TYPE_SKIPLIST);
			
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("x");
			//fields.add("y");
			//fields.add("z");
			
			index.setFields(fields);
			
			try {
				database.createIndex(Point.class, index);
			} catch (ArangoDb4JException e) {
			}
		}		
	}
	
	private PointRepository repo;
	
	protected void setUp() {
		super.setUp();
		
		try {
			database.deleteCollection(Point.class);
		}
		catch (Exception e) {
			
		}
		
		repo = new PointRepository(database);
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
			repo.create(p);
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
			assertTrue(true);
		}
	}
	
	public void test_readDocument () {
		// create
		Point p = createPoint(7, 12);
		String key = p.getKey();
		
		// read
		Point p2 = null;		
		try {
			p2 = repo.read(key);
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
			p2 = repo.read(key);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
		}		
		assertNotNull(p2);
		
		// update
		p.setX(9);
		try {
			repo.update(p);
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
			repo.delete(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		// read
		Point p2 = null;		
		try {
			// error
			p2 = repo.read(key);
		} catch (ArangoDb4JException e) {
		}
		
		assertNull(p2);
	}

	public void test_deleteDocument2times () {
		// create
		Point p = createPoint(8, 14);

		// delete
		try {
			repo.delete(p);
		} catch (ArangoDb4JException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		// delete
		try {
			// error
			repo.delete(p);
			assertTrue(false);
		} catch (ArangoDb4JException e) {
		}
	}
	
	private void createNumPoints (int num) {
		for (int i = 0; i < num; ++i) {
			createPoint(5 + i, 10 + i); 						
		}
	}
	
	
	public void test_getAllByQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = repo.getQuery();
		
		int count = 0;
		Iterator<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(num, count);
	}

	public void test_getAllByQueryWithLimit () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = repo.getQuery();
		query.limit(5);
		
		int count = 0;
		Iterator<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(5, count);
	}

	public void test_databaseGetAll () {
		int num = 10;
		createNumPoints(num);

		Cursor<Point> cursor = null;
		try {
			cursor = repo.getAll();
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
		
		ArangoDbQuery<Point> query = repo.getQuery();
		query.has("x", 7);
		
		int count = 0;
		Iterator<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(1, count);
	}

	public void test_hasQuery2 () {
		int num = 10;
		createNumPoints(num);
		
		int count = 0;
		Cursor<Point> iter = repo.getAllByKeyValue("x", 7);
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(1, count);
	}
	
	public void test_compareQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = repo.getQuery();
		query.has("y", 15, PropertyFilter.Compare.GREATER_THAN);
		query.has("y", 17, PropertyFilter.Compare.LESS_THAN_EQUAL);
		
		int count = 0;
		Iterator<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(2, count);
	}

	public void test_intervalQuery () {
		int num = 10;
		createNumPoints(num);
		
		ArangoDbQuery<Point> query = repo.getQuery();
		query.interval("y", 16, 18);
		
		int count = 0;
		Iterator<Point> iter =  query.execute();
		while (iter.hasNext()) {
			iter.next();
			++count;
		}		
		assertEquals(2, count);
	}
	
}
