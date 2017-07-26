import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTHiddenClass;
import intermediateModel.structure.ASTIf;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.IndexingSyncBlock;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexSyncBlock;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import parser.Java2AST;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestBugs {


	@Before
	public void setUp() throws Exception {
		MongoOptions.getInstance().setDbName("intermediate_model_" + this.getClass().getSimpleName());
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void bugHiddenClasses() throws Exception {
		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/HiddenClass.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		//one class
		assertEquals(v.listOfClasses.size(), 1);
		//two methods
		assertEquals(c.getMethods().size(), 2);
		//two hidden classes
		final int[] n_hidden = {0};
		final ASTHiddenClass[] hidden = { null, null};
		c.visit(new DefaultASTVisitor(){
			@Override
			public void enterASTHiddenClass(ASTHiddenClass astHiddenClass) {
				hidden[n_hidden[0]] = astHiddenClass;
				n_hidden[0]++;
			}
		});
		assertEquals(n_hidden[0], 2);
		//first hidden 3 methods
		assertEquals(hidden[0].getMethods().size(), 3);
		//second hidden 1 method
		assertEquals(hidden[1].getMethods().size(), 1);
	}

	@Test
	public void bugHiddenClassesStmsToCorrectMethod() throws Exception {
		//first method
		String f =  TestBugs.class.getClassLoader().getResource("bugs/Strange.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast,f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
	}

	@Test
	public void notWellParsed() throws Exception {
		String f =  TestBugs.class.getClassLoader().getResource("bugs/NotWellParsed.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast,f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		assertEquals(c.getMethods().size(), 1);
		IASTMethod m = c.getMethods().get(0);
		assertEquals(m.getStms().size(), 1);
		assertEquals(m.getStms().get(0).getClass(), ASTIf.class);
		ASTIf elm = (ASTIf) m.getStms().get(0);
		assertEquals(elm.getIfBranch().getStms().size(), 6);
	}


	@Test
	public void CheckAccessibilitiesFromOutside() throws Exception {
		String f = TestBugs.class.getClassLoader().getResource("checkOutsideAccessibilities/FirstCase_1.java").getPath();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		ASTClass c = v.listOfClasses.get(0);
		IndexingSyncBlock indexingSyncBlock = new IndexingSyncBlock();
		List<IndexSyncBlock> i = indexingSyncBlock.index(c, true);
		assertEquals(i.size(), 1);
		assertFalse(i.get(0).isAccessibleFromOutside());
		assertTrue(i.get(0).isAccessibleWritingFromOutside());

		f = TestBugs.class.getClassLoader().getResource("checkOutsideAccessibilities/FirstCase_2.java").getPath();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		c = v.listOfClasses.get(0);
		indexingSyncBlock = new IndexingSyncBlock();
		i = indexingSyncBlock.index(c, true);
		assertEquals(i.size(), 1);
		assertFalse(i.get(0).isAccessibleFromOutside());
		assertTrue(i.get(0).isAccessibleWritingFromOutside());

		f = TestBugs.class.getClassLoader().getResource("checkOutsideAccessibilities/FirstCase_3.java").getPath();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		c = v.listOfClasses.get(0);
		indexingSyncBlock = new IndexingSyncBlock();
		i = indexingSyncBlock.index(c, true);
		assertEquals(i.size(), 1);
		assertFalse(i.get(0).isAccessibleFromOutside());
		assertFalse(i.get(0).isAccessibleWritingFromOutside());

		f = TestBugs.class.getClassLoader().getResource("checkOutsideAccessibilities/SecondCase_1.java").getPath();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		c = v.listOfClasses.get(0);
		indexingSyncBlock = new IndexingSyncBlock();
		i = indexingSyncBlock.index(c, true);
		assertEquals(i.size(), 1);
		assertTrue(i.get(0).isAccessibleFromOutside());
		assertFalse(i.get(0).isAccessibleWritingFromOutside());

		f = TestBugs.class.getClassLoader().getResource("checkOutsideAccessibilities/SecondCase_2.java").getPath();
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		//we have only one class
		c = v.listOfClasses.get(0);
		indexingSyncBlock = new IndexingSyncBlock();
		i = indexingSyncBlock.index(c, true);
		assertEquals(i.size(), 1);
		assertFalse(i.get(0).isAccessibleFromOutside());
		assertFalse(i.get(0).isAccessibleWritingFromOutside());
	}
}
