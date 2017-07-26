import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.IndexingFile;
import intermediateModelHelper.indexing.structure.IndexData;
import intermediateModelHelper.indexing.structure.IndexMethod;
import org.eclipse.jdt.core.dom.CompilationUnit;
import parser.Java2AST;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestIndexing {


	public List<ASTClass> init(String filename) throws Exception {
		Java2AST a = new Java2AST( filename , true);

		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, filename);
		ast.accept(v);
		return v.listOfClasses;
	}

	@Test
	public void TestExportChangesJob() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/ExportChangesJob.java").getFile();
		List<ASTClass> cs = init(filename);
		{
			IndexingFile ii = new IndexingFile();
			IndexData index = ii.index(cs.get(0));
			List<IndexMethod> methods = index.getListOfMethods();
			List<IndexMethod> syncMethods = index.getListOfSyncMethods();
			List timedMethods = index.getListOfTimedMethods();
			List<String> compare = new ArrayList<>();
			for(IndexMethod i : methods){
				compare.add(i.getName());
			}
			assertArrayEquals(compare.toArray(), new String[]{"ExportChangesJob", "run", "findMaxDistinctChanges", "findMaxChanges"});
			assertArrayEquals(syncMethods.toArray(), new String[]{});
			assertArrayEquals(timedMethods.toArray(), new String[]{});
		}
	}

	@Test
	public void TestFailoverTimeoutTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/FailoverTimeoutTest.java").getFile();
		List<ASTClass> cs = init(filename);
		{
			IndexingFile ii = new IndexingFile();
			IndexData index = ii.index(cs.get(0));
			List<IndexMethod> methods = index.getListOfMethods();
			List<IndexMethod> syncMethods = index.getListOfSyncMethods();
			List timedMethods = index.getListOfTimedMethods();
			List<String> compare = new ArrayList<>();
			for(IndexMethod i : methods){
				compare.add(i.getName());
			}
			assertArrayEquals(compare.toArray(), new String[]{"setUp", "tearDown", "getTransportUri",
					"testTimoutDoesNotFailConnectionAttempts","safeClose", "testTimeout", "testInterleaveAckAndException",
					"testInterleaveTxAndException", "doTestInterleaveAndException",
					//"onException","run", //hidden method
					"testUpdateUris"
			});
			assertArrayEquals(syncMethods.toArray(), new String[]{});
			assertArrayEquals(timedMethods.toArray(), new String[]{});
		}
	}

	@Test
	public void TestJavaTimerExampleTask() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/JavaTimerExampleTask.java").getFile();
		List<ASTClass> cs = init(filename);
		{
			IndexingFile ii = new IndexingFile();
			IndexData index = ii.index(cs.get(0));
			List<IndexMethod> methods = index.getListOfMethods();
			List<IndexMethod> syncMethods = index.getListOfSyncMethods();
			List timedMethods = index.getListOfTimedMethods();
			List<String> compare = new ArrayList<>();
			for(IndexMethod i : methods){
				compare.add(i.getName());
			}
			List<String> compareSync = new ArrayList<>();
			for(IndexMethod i : syncMethods){
				compareSync.add(i.getName());
			}
			assertArrayEquals(compare.toArray(), new String[]{"run", "main"});
			assertArrayEquals(compareSync.toArray(), new String[]{"run"});
			assertArrayEquals(timedMethods.toArray(), new String[]{});
		}
	}

	@Test //(expected = AssertionError.class)
	public void TestMCGroupImpl() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/MCGroupImpl.java").getFile();
		List<ASTClass> cs = init(filename);
		{
			IndexingFile ii = new IndexingFile();
			IndexData index = ii.index(cs.get(0));
			List<IndexMethod> methods = index.getListOfMethods();
			List<IndexMethod> syncMethods = index.getListOfSyncMethods();
			List timedMethods = index.getListOfTimedMethods();
			List<String> compare = new ArrayList<>();
			for(IndexMethod i : methods){
				compare.add(i.getName());
			}
			assertArrayEquals(compare.toArray(), new String[]{
				"getSingleton", "setSuspended", "MCGroupImpl",
				//"perform",//hidden
				"setInstanceSuspended",
				//"run","runSupport", //hidden
				"processNetworkInterfaces",
				//"runSupport","run","run", //hidden
				"getControlPort", "interfaceSelected", "validNetworkAddress", "sendToGroup",
				//"runSupport",//hidden
				"sendToGroupSupport", "sendToGroup",
				//"runSupport",//hidden
				"sendToGroupSupport", "handleSocket", "receivePacket", "sendToMember",
			});
			assertArrayEquals(syncMethods.toArray(), new String[]{});
			assertArrayEquals(timedMethods.toArray(), new String[]{});
		}
	}

	@Test
	public void TestProjectServiceImpl() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/ProjectServiceImpl.java").getFile();
		List<ASTClass> cs = init(filename);

	}

	@Test
	public void TestSmallTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/SmallTest.java").getFile();
		List<ASTClass> cs = init(filename);

	}

	@Test
	public void TestsocketTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/socketTest.java").getFile();
		List<ASTClass> cs = init(filename);

	}

	@Test
	public void TestTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/Test.java").getFile();
		List<ASTClass> cs = init(filename);

	}

	@Test
	public void TesttestLambdas() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/testLambdas.java").getFile();
		List<ASTClass> cs = init(filename);

	}

	@Test
	public void TestTimerEvent() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/TimerEvent.java").getFile();
		List<ASTClass> cs = init(filename);

	}

	@Test
	public void TestUPnPImpl() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/UPnPImpl.java").getFile();
		List<ASTClass> cs = init(filename);

	}


}
