import PCFG.creation.IM2PCFG;
import PCFG.structure.CFG;
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
public class TestAnonymousClasses {
	final String DB_NAME = "TestAnonymousClasses";

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
	public void TestVisitedAnonymClasses() throws Exception {
		String f =  TestAnonymousClasses.class.getClassLoader().getResource("anonym/TagPropertyConstraintHandler.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		//f =  "/Users/giovanni/repository/java-xal/evaluation-vuze/src/main/resources/vuze/com/aelitis/azureus/core/dht/transport/udp/impl/DHTTransportUDPContactImpl.java";
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("apply",
				Arrays.asList()
		));

		p.addClass(c1, c1.getMethodBySignature("apply",
				Arrays.asList()
		));
		PCFG graph = p.buildPCFG();
		graph.optimize();

		assertEquals(graph.getCFG().size(), 2);
		for(CFG cfg : graph.getCFG()){
			assertEquals(cfg.getAnonNodes().size(), 1);
		}
	}
}
