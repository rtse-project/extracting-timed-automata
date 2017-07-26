import PCFG.creation.IM2PCFG;
import PCFG.structure.PCFG;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.IndexingProject;
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
public class TestSynchronization {

	final String DB_NAME = "TestSynchronization";

	@Before
	public void setUp() throws Exception {
		MongoOptions.getInstance().setDbName(DB_NAME);
		MongoConnector.getInstance(DB_NAME).drop();
		MongoConnector.getInstance(DB_NAME).ensureIndexes();
	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void TestSameVarButNotAccessibleFromOutside() throws Exception {
		String f =  TestSynchronization.class.getClassLoader().getResource("shouldNotSync/MagnetPlugin.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		f =  TestSynchronization.class.getClassLoader().getResource("shouldNotSync/MagnetURIHandlerImpl.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("_downloadSupport",
				Arrays.asList("MagnetPluginProgressListener, byte[], String, InetSocketAddress[], long, int".split(", "))
		));
		p.addClass(c1, c1.getMethodBySignature("process",
				Arrays.asList("String, BufferedReader, OutputStream".split(", "))
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getSyncNodes().size(), 12 + 7 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 0 );

	}

	@Test
	public void TestSameClassOnThis() throws Exception {
		String f =  TestSynchronization.class.getClassLoader().getResource("airavata/WaitDialog.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		f =  TestSynchronization.class.getClassLoader().getResource("airavata/WaitDialog.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("show",
				Arrays.asList()
		));
		p.addClass(c1, c1.getMethodBySignature("show",
				Arrays.asList()
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getSyncNodes().size(), 1 + 1 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 1 );

	}

	@Test
	public void TestSameClassOnDotClass() throws Exception {
		String f =  TestSynchronization.class.getClassLoader().getResource("airavata/WorkflowEnactmentService.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("getInstance",
				Arrays.asList()
		));
		p.addClass(c1, c1.getMethodBySignature("getInstance",
				Arrays.asList()
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getSyncNodes().size(), 1 + 1 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 1 );

	}

	@Test
	public void TestSameOutsideVariable() throws Exception {
		String f =  TestSynchronization.class.getClassLoader().getResource("airavata/HPCPullMonitor.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		f = TestSynchronization.class.getClassLoader().getResource("airavata/CommonUtils.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("run",
				Arrays.asList()
		));
		p.addClass(c1, c1.getMethodBySignature("addMonitortoQueue",
				Arrays.asList("BlockingQueue, MonitorID, JobExecutionContext".split(", "))
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getSyncNodes().size(), 1 + 1 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 1 );
	}

	@Test
	public void TestSameNameDifferentClasses() throws Exception {
		String f =  TestSynchronization.class.getClassLoader().getResource("airavata/PredicatedTaskRunner_co.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		f = TestSynchronization.class.getClassLoader().getResource("airavata/PredicatedTaskRunner_wf.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("addIdleTask",
				Arrays.asList()
		));
		p.addClass(c1, c1.getMethodBySignature("addIdleTask",
				Arrays.asList()
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getSyncNodes().size(), 1 + 1 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 0 );
	}

	@Test
	public void TestSameSyncCallNameButInDifferentClasses() throws Exception {

		String f =  TestSynchronization.class.getClassLoader().getResource("shouldNotSync/JarResource.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);

		//add the second method
		f = TestSynchronization.class.getClassLoader().getResource("shouldNotSync/URLResource.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		String base_path = f.substring(0, f.lastIndexOf("/") + 1);
		IndexingProject index = new IndexingProject(DB_NAME);
		index.indexProject(base_path, true);
		index.indexSyncCall(base_path,false);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getMethodBySignature("exists",
				Arrays.asList()
		));
		p.addClass(c1, c1.getMethodBySignature("exists",
				Arrays.asList()
		));
		MongoConnector.getInstance().ensureIndexes();
		PCFG g = p.buildPCFG();

		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 0 );
	}


}
