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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestBugs {

	@Before
	public void setUp() throws Exception {
		MongoOptions.getInstance().setDbName("testPCFGBugs");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void TestBug23() throws Exception {
		IM2PCFG p = new IM2PCFG();
		p.setForceReindex(true);

		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_1.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		String method = "run";
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		//add the second method
		f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_2.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		c = v.listOfClasses.get(0);
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		// build
		PCFG g = p.buildPCFG();

		//g.toGraphViz(false);

		assertEquals(g.getV().size(), 6);
		assertEquals(g.getE().size(), 4);
		assertEquals(g.getSyncNodes().size(), 0 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 4 );

	}

	@Test
	public void TestBug22() throws Exception {
		IM2PCFG p = new IM2PCFG();

		String directory = TestBugs.class.getClassLoader().getResource("bugs/XString.java").getPath();
		directory = directory.substring(0, directory.lastIndexOf("/")+1);


		IndexingProject indexing = new IndexingProject();
		indexing.indexProject(directory, true);

		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_3.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		String method = "run";
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		//add the second method
		f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_2.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		c = v.listOfClasses.get(0);
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		// build
		PCFG g = p.buildPCFG();

		//System.out.println(g.toGraphViz(false));

		assertEquals(g.getV().size(), 6);
		assertEquals(g.getE().size(), 4);
		assertEquals(g.getSyncNodes().size(), 0 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 2 );
	}

	@Test //(expected = AssertionError.class)
	public void TestBug22_package_error() throws Exception {
		IM2PCFG p = new IM2PCFG();

		String directory = TestBugs.class.getClassLoader().getResource("bugs/XString_v2.java").getPath();
		directory = directory.substring(0, directory.lastIndexOf("/")+1);


		IndexingProject indexing = new IndexingProject();
		indexing.indexProject(directory, true);

		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_4.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		String method = "run";
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		//add the second method
		f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_2.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		c = v.listOfClasses.get(0);
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		// build
		PCFG g = p.buildPCFG();

		//System.out.println(g.toGraphViz(false));

		assertEquals(g.getV().size(), 6);
		assertEquals(g.getE().size(), 4);
		assertEquals(g.getSyncNodes().size(), 0 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 2 );
	}

	@Test
	public void TestBug22_package_star() throws Exception {
		IM2PCFG p = new IM2PCFG();

		String directory = TestBugs.class.getClassLoader().getResource("bugs/XString_v2.java").getPath();
		directory = directory.substring(0, directory.lastIndexOf("/")+1);


		IndexingProject indexing = new IndexingProject();
		indexing.indexProject(directory, true);

		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_5.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		String method = "run";
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		//add the second method
		f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_2.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		c = v.listOfClasses.get(0);
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		// build
		PCFG g = p.buildPCFG();

		//System.out.println(g.toGraphViz(false));

		assertEquals(g.getV().size(), 6);
		assertEquals(g.getE().size(), 4);
		assertEquals(g.getSyncNodes().size(), 0 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 2 );
	}

	@Test
	public void TestBug22_interfaces() throws Exception {
		IM2PCFG p = new IM2PCFG();

		String directory = TestBugs.class.getClassLoader().getResource("bugs/XString_v3.java").getPath();
		directory = directory.substring(0, directory.lastIndexOf("/")+1);


		IndexingProject indexing = new IndexingProject();
		indexing.indexProject(directory, true);

		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_6.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		String method = "run";
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		//add the second method
		f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_7.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		c = v.listOfClasses.get(0);
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		// build
		PCFG g = p.buildPCFG();

		//System.out.println(g.toGraphViz(false));

		assertEquals(g.getV().size(), 6);
		assertEquals(g.getE().size(), 4);
		assertEquals(g.getSyncNodes().size(), 0 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 1 );
	}

	@Test
	public void TestBug22_interfaces_not_compatible() throws Exception {
		IM2PCFG p = new IM2PCFG();

		String directory = TestBugs.class.getClassLoader().getResource("bugs/XString_v3.java").getPath();
		directory = directory.substring(0, directory.lastIndexOf("/")+1);


		IndexingProject indexing = new IndexingProject();
		indexing.indexProject(directory, true);

		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_8.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		String method = "run";
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		//add the second method
		f =  TestBugs.class.getClassLoader().getResource("bugs/Thread_9.java").getFile();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		c = v.listOfClasses.get(0);
		p.addClass(c, c.getMethodBySignature(method, new ArrayList<>()));

		// build
		PCFG g = p.buildPCFG();

		//System.out.println(g.toGraphViz(false));

		assertEquals(g.getV().size(), 6);
		assertEquals(g.getE().size(), 4);
		assertEquals(g.getSyncNodes().size(), 0 );
		assertEquals(g.getCFG().size(), 2 );
		assertEquals(g.getESync().size(), 2 );
	}
}
