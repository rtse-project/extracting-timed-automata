import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.EnvBase;
import intermediateModelHelper.envirorment.EnvExtended;
import intermediateModelHelper.envirorment.EnvParameter;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.javatuples.Triplet;
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
public class TestEnvirorment {
    String filename = "env/AttributeTimeRelated.java";
    List<ASTClass> intemediateModel;

	public class TestParseIM extends ParseIM{
		@Override
		protected EnvBase createBaseEnv(ASTClass c) {
			return super.createBaseEnv(c);
		}
	}

	public class TestMethodParseIM extends ParseIM{
		public Env getEnv(IASTMethod method){
			Env e = new Env();
			//analyzeMethod(method, e);
			Env eMethod = new EnvParameter(e, method.getName());
			eMethod = CheckExpression.checkPars(method.getParameters(), eMethod);
			return eMethod;
		}
	}

	@Before
	public void setUp() throws Exception {
		MongoOptions.getInstance().setDbName("testEnv");
		MongoConnector.getInstance().drop();

	}

	@After
	public void tearDown() throws Exception {
		MongoConnector.getInstance().close();
	}

	public List<ASTClass> init(String filename) throws Exception {
		Java2AST a = new Java2AST( filename, true );
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, filename);
		ast.accept(v);
		return v.listOfClasses;
	}


	@Test
	public void TestAttributeEnv() throws Exception {
		String filename;
		ASTClass cs;
		String[] vars;
		Env e;
		filename = getClass().getClassLoader().getResource("env/Rectangle.java").getFile();
		cs = init(filename).get(0);
		vars = new String[] {"length","width"};
		e = (new TestParseIM()).createBaseEnv(cs);
		for(String v : vars){
			assertTrue(e.isClassAttribute(v));
		}
		filename = getClass().getClassLoader().getResource("env/Square.java").getFile();
		cs = init(filename).get(0);
		vars = new String[] {"size"};
		e = (new TestParseIM()).createBaseEnv(cs);
		for(String v : vars){
			assertTrue(e.isClassAttribute(v));
		}
		filename = getClass().getClassLoader().getResource("env/Shape.java").getFile();
		cs = init(filename).get(0);
		vars = new String[] {"x","y"};
		e = (new TestParseIM()).createBaseEnv(cs);
		for(String v : vars){
			assertTrue(e.isClassAttribute(v));
		}
	}

	@Test
	public void TestMethodParEnv() throws Exception {
		String filename;
		ASTClass cs;
		String[] vars;
		Env e;
		filename = getClass().getClassLoader().getResource("env/Rectangle.java").getFile();
		cs = init(filename).get(0);
		vars = new String[] {"length"};
		e = (new TestMethodParseIM()).getEnv(cs.getFirstMethodByName("setLength"));
		for(String v : vars){
			assertTrue(e.isMethodParameter(v));
		}
		assertFalse(e.isMethodParameter("width"));
		filename = getClass().getClassLoader().getResource("env/Square.java").getFile();
		cs = init(filename).get(0);
		vars = new String[] {"size"};
		e = (new TestMethodParseIM()).getEnv(cs.getFirstMethodByName("Square"));
		for(String v : vars){
			assertTrue(e.isMethodParameter(v));
		}

	}

	@Test
	public void TestEnvExtended() throws Exception {
		IndexingProject indexing = new IndexingProject();
		String directory = getClass().getClassLoader().getResource("env/AttributeTimeRelated.java").getFile();
		String base_path = directory.substring(0, directory.lastIndexOf("/")+1);
		indexing.setSkipTest(false);
		indexing.indexProject(base_path);
		MongoConnector.getInstance().ensureIndexes();
		final Env[] out = new Env[1];
		ParseIM getEnv = new ParseIM (){
			@Override
			public void start(ASTClass c) {
				super.start(c);
				Env e = super.createBaseEnv(c);
				e.setPrev( EnvExtended.getExtendedEnv(c));
				out[0] = e;
			}
		};

		List<ASTClass> cs;
		EnvExtended e;
		cs = JDTVisitor.parse( getClass().getClassLoader().getResource("env/Shape.java").getFile(), directory);
		getEnv.start(cs.get(0));
		assertTrue(out[0].getPrev().getPrev() == null);
		assertTrue(out[0].existVarName("x"));
		assertTrue(out[0].existVarName("y"));
		assertTrue(out[0].getVar("x").getType().equals("int"));
		assertTrue(out[0].getVar("y").getType().equals("int"));
		e = out[0].getExtendedEnv();
		assertEquals(e.getClassName(), "Object");
		assertEquals(e.getPackageName(), "");

		cs = JDTVisitor.parse( getClass().getClassLoader().getResource("env/Rectangle.java").getFile(), directory);
		getEnv.start(cs.get(0));
		assertTrue(out[0].getPrev() instanceof EnvExtended);
		assertTrue(out[0].isInherited("x"));
		assertTrue(out[0].isInherited("y"));
		assertTrue(out[0].getPrev().existVarName("x"));
		assertTrue(out[0].getPrev().existVarName("y"));
		assertTrue(out[0].getPrev().getVar("x").getType().equals("int"));
		assertTrue(out[0].getPrev().getVar("y").getType().equals("int"));
		assertTrue(out[0].existVarName("length"));
		assertTrue(out[0].existVarName("width"));
		assertTrue(out[0].getVar("length").getType().equals("double"));
		assertTrue(out[0].getVar("length").getType().equals("double"));
		e = out[0].getExtendedEnv();
		assertEquals(e.getClassName(), "Shape");
		assertEquals(e.getPackageName(), "");
		e = out[0].getExtendedEnv("x");
		assertEquals(e.getClassName(), "Shape");
		assertEquals(e.getPackageName(), "");

		cs = JDTVisitor.parse( getClass().getClassLoader().getResource("env/Square.java").getFile(), directory);
		getEnv.start(cs.get(0));
		assertTrue(out[0].getPrev() instanceof EnvExtended);
		assertTrue(out[0].getPrev().getPrev() instanceof EnvExtended);
		assertTrue(out[0].isInherited("x"));
		assertTrue(out[0].isInherited("y"));
		assertTrue(out[0].isInherited("length"));
		assertTrue(out[0].isInherited("width"));
		assertTrue(out[0].existVarName("x"));
		assertTrue(out[0].existVarName("y"));
		assertTrue(out[0].getVar("x").getType().equals("int"));
		assertTrue(out[0].getVar("y").getType().equals("int"));
		assertTrue(out[0].existVarName("length"));
		assertTrue(out[0].existVarName("width"));
		assertTrue(out[0].getVar("length").getType().equals("double"));
		assertTrue(out[0].getVar("length").getType().equals("double"));
		assertTrue(out[0].existVarName("size"));
		assertTrue(out[0].getVar("size").getType().equals("int"));
		e = out[0].getExtendedEnv();
		assertEquals(e.getClassName(), "Rectangle");
		assertEquals(e.getPackageName(), "");
		e = e.getExtendedEnv();
		assertEquals(e.getClassName(), "Rectangle");
		assertEquals(e.getPackageName(), "");
		e = out[0].getExtendedEnv("x");
		assertEquals(e.getClassName(), "Shape");
		assertEquals(e.getPackageName(), "");


	}

	private boolean check(int line, String message, Class _class, List<Triplet<String, IASTStm, Class>> constraints ){
		boolean flag = false;
		for(Triplet<String, IASTStm, Class> c : constraints){
			if(c.getValue0().equals(message) &&
			   c.getValue1().getLine() == line &&
			   c.getValue2().equals(_class)){
				flag = true;
			}
		}
		return flag;
	}
}
