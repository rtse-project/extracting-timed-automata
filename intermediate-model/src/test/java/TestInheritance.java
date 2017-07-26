import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.types.DataTreeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestInheritance {


	@Before
	public void setUp() throws Exception {
		String directory = TestInheritance.class.getClassLoader().getResource("inheritance/Shape.java").getPath();
		directory = directory.substring(0, directory.lastIndexOf("/")+1);


		MongoOptions.getInstance().setDbName("testInheritance");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();

		IndexingProject indexing = new IndexingProject();
		indexing.setSkipTest(false);
		indexing.indexProject(directory, false);



	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void TestInheritance() throws Exception {
		assertTrue(DataTreeType.checkEqualsTypes("Square","Rectangle", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Rectangle","Square", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Square","Rectangle", "", ""));
		assertFalse(DataTreeType.checkCompatibleTypes("Rectangle","Square", "", ""));

		assertFalse(DataTreeType.checkEqualsTypes("Square","Circle", "", ""));
		assertFalse(DataTreeType.checkEqualsTypes("Rectangle","Circle", "", ""));
		assertFalse(DataTreeType.checkEqualsTypes("Circle","Square", "", ""));
		assertFalse(DataTreeType.checkEqualsTypes("Circle","Rectangle", "", ""));

		assertTrue(DataTreeType.checkEqualsTypes("Circle","Shape", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Rectangle","Shape", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Square","Shape", "", ""));

		assertTrue(DataTreeType.checkEqualsTypes("Shape","Circle", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Shape","Rectangle", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Shape","Square", "", ""));

		assertTrue(DataTreeType.checkCompatibleTypes("Circle","Shape", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Rectangle","Shape", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Square","Shape", "", ""));


		assertFalse(DataTreeType.checkCompatibleTypes("Shape","Circle", "", ""));
		assertFalse(DataTreeType.checkCompatibleTypes("Shape","Rectangle", "", ""));
		assertFalse(DataTreeType.checkCompatibleTypes("Shape","Square", "", ""));

		assertTrue(DataTreeType.checkEqualsTypes("Square","Square", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Square","Square", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Rectangle","Rectangle", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Rectangle","Rectangle", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Shape","Shape", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Shape","Shape", "", ""));
		assertTrue(DataTreeType.checkEqualsTypes("Circle","Circle", "", ""));
		assertTrue(DataTreeType.checkCompatibleTypes("Circle","Circle", "", ""));

	}
}
