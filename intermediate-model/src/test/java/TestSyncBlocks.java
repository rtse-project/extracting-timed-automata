import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexSyncBlock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestSyncBlocks {

	String file = TestSyncBlocks.class.getClassLoader().getResource("exprTypesSync/Thread_1.java").getPath();
	String _dir = "";
	@Before
	public void setUp() throws Exception {

		String directory = file.substring(0, file.lastIndexOf("/")+1);
		_dir = directory;

		MongoOptions.getInstance().setDbName("testSyncBlocks");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();

		IndexingProject indexing = new IndexingProject();
		indexing.setSkipTest(false);
		indexing.indexProject(directory, true);
		indexing.indexSyncBlock(directory, false);
		
	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void TestSyncs() throws Exception {
		List<ASTClass> classes = JDTVisitor.parse(file, _dir);
		ASTClass c = classes.get(0);
		List<IndexSyncBlock> ret = MongoConnector.getInstance().getSyncBlockIndex(c);
		assertEquals(5, ret.size());
		IndexSyncBlock s;
		//1st
		s = ret.get(0);
		assertEquals("this", s.getExpr());
		assertEquals("test", s.getExprPkg());
		assertEquals("Thread_1", s.getExprType());
		//2nd
		s = ret.get(1);
		assertEquals("Thread_1.class", s.getExpr());
		assertEquals("test", s.getExprPkg());
		assertEquals("Thread_1", s.getExprType());
		//3rd
		s = ret.get(2);
		assertEquals("var", s.getExpr());
		assertEquals("test", s.getExprPkg());
		assertEquals("Thread_2", s.getExprType());
		//4th
		s = ret.get(3);
		assertEquals("var.minPrime", s.getExpr());
		assertEquals("java.lang", s.getExprPkg());
		assertEquals("ObjectTypes", s.getExprType());
		//5th
		s = ret.get(4);
		assertEquals("var.init(xyz)", s.getExpr());
		assertEquals("", s.getExprPkg());
		assertEquals("String", s.getExprType());
	}
}
