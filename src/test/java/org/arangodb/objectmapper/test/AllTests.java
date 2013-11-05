package org.arangodb.objectmapper.test;

import junit.framework.*;

public class AllTests {

	  public static Test suite() {
		    TestSuite suite = new TestSuite();
		    suite.addTestSuite(CollectionTest.class);
		    suite.addTestSuite(DatabaseCRUDTest.class);
		    suite.addTestSuite(DatabaseTest.class);
		    suite.addTestSuite(QueryTest.class);
		    suite.addTestSuite(IndexTest.class);
		    suite.addTestSuite(RepositoryTest.class);

		    return suite;
		  }	
}
