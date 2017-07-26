import PCFG.creation.IM2PCFG;
import PCFG.structure.PCFG;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import parser.Java2AST;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestSyncNode {

	final String DB_NAME = "TestSyncNode";

	@Before
	public void setUp() throws Exception {
		MongoOptions.getInstance().setDbName(DB_NAME);
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void ExampleFromVuze() throws Exception {
		String f =  TestSyncNode.class.getClassLoader().getResource("basic/ThreadPool.java").getFile();
		Java2AST a = new Java2AST(f,  true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		f =  TestSyncNode.class.getClassLoader().getResource("basic/NetworkGlueLoopBack.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("releaseManual",
				Arrays.asList("ThreadPoolTask")
		));
		p.addClass(c1, c1.getMethodBySignature("NetworkGlueLoopBack",
				Arrays.asList("NetworkGlueListener")
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getV().size(), 47);
		assertEquals(g.getE().size(), 24);
		assertEquals(g.getSyncNodes().size(), 3 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 0 );

	}
}
