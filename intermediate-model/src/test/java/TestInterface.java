import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexData;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.javatuples.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import parser.Java2AST;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestInterface {

	@Before
	public void setUp() throws Exception {

		MongoOptions.getInstance().setDbName("testInterface");
		MongoConnector.getInstance().drop();
		MongoConnector.getInstance().ensureIndexes();
	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	@Test
	public void TestInterface() throws Exception {
		String f =  TestBugs.class.getClassLoader().getResource("exprTypesSync/IXString.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c = v.listOfClasses.get(0);
		assertTrue(c.isInterface());
	}

	@Test
	public void TestInterfaceTypeResolution() throws Exception {
		String f =  TestBugs.class.getClassLoader().getResource("interface/ITest.java").getFile();
		f = f.substring(0, f.lastIndexOf("/") + 1);

		IndexingProject indexing = new IndexingProject();
		indexing.setSkipTest(false);
		indexing.indexProject(f, false);

		MongoConnector mongo = MongoConnector.getInstance();
		List<IndexData> data = mongo.resolveClassImplementingInterface("ITest","org.test.Interface");
		assertEquals(data.size(), 6);
		List<Pair<String,String>> classNames = new ArrayList<>();
		classNames.add(new Pair<>("org.test.Interface","ITest"));
		classNames.add(new Pair<>("org.test.Interface","ITestComplex"));
		classNames.add(new Pair<>("org.test.Impl","Test"));
		classNames.add(new Pair<>("org.test.Impl","TestSync"));
		classNames.add(new Pair<>("org.test.Impl2","TestExt"));
		classNames.add(new Pair<>("org.test.Impl2","TestExtExt"));

		for(IndexData d : data){
			Pair<String,String> p = new Pair<>(d.getClassPackage(),d.getClassName());
			if(classNames.contains(p)){
				classNames.remove(p);
			}
		}
		assertEquals(classNames.size(),0);
	}
}
