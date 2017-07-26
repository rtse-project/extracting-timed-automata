import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.heuristic.definition.TimeInSignature;
import intermediateModelHelper.heuristic.definition.SetTimeout;
import intermediateModelHelper.heuristic.definition.TimeoutResources;
import intermediateModelHelper.indexing.IndexingFile;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexData;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestHeuristics {

	public List<ASTClass> init(String filename) throws Exception {
		String dir = filename.substring(0, filename.lastIndexOf("/"));
		return JDTVisitor.parse(filename,dir);
	}

	@Test
	public void TestExportChangesJob() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/ExportChangesJob.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));


		List<Constraint> constraints = ah.getTimeConstraint();
		assertEquals(constraints.size(), 0);
	}

	@Test
	public void TestFailoverTimeoutTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/FailoverTimeoutTest.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();



		assertTrue(check(
				103,
				"assertTrue(duration > 3000);",
				TimeoutResources.class,
				constraints
		));
		assertTrue(check(
				195,
				"TimeUnit.MILLISECONDS.sleep(Math.max(0, sleepMillis.addAndGet(-50)));",
				TimeInSignature.class,
				constraints
		));

		assertEquals(constraints.size(), 2);

	}

	@Test
	public void TestShowBug18() throws Exception {
		String filename = getClass().getClassLoader().getResource("env/AttributeTimeRelated.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.set__DEBUG__(true);
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));
		MongoOptions.getInstance().setDbName("TestShowBug18");
		IndexingFile indexing = new IndexingFile();
		IndexData data = indexing.index(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();

		assertTrue(check(
				14,
				"paused_on > 0 && started_on > 0",
				TimeoutResources.class,
				constraints
		));

		assertEquals(constraints.size(), 1);

		MongoConnector.getInstance().close();
	}

	@Test
	public void TestJavaTimerExampleTask() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/JavaTimerExampleTask.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();


		assertTrue(check(
				16,
				"Thread.sleep(4000);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				35,
				"timer.schedule(task, 0, 5000);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				38,
				"task.wait(100);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				43,
				"Thread.sleep(10000);",
				TimeInSignature.class,
				constraints
		));

		assertEquals(constraints.size(), 4);

	}

	@Test
	public void TestMCGroupImpl() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/MCGroupImpl.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();


		assertTrue(check(
				817,
				"socket.receive( packet )",
				SetTimeout.class,
				constraints
		));

		assertEquals(constraints.size(), 1);

	}

	@Test
	public void TestProjectServiceImpl() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/ProjectServiceImpl.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();


		assertTrue(check(
				349,
				"Thread.sleep(500+((int)Math.random()*1000));",
				TimeInSignature.class,
				constraints
		));
		assertEquals(constraints.size(), 1);

	}

	@Test
	public void TestSmallTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/SmallTest.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();
		assertEquals(constraints.size(), 0);
	}

	@Test
	public void TestsocketTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/socketTest.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();


		assertTrue(check(
				35,
				"socket.connect( new InetSocketAddress( _address, 2190 ), 5000 );",
				TimeInSignature.class,
				constraints
		));
		assertEquals(constraints.size(), 1);


	}

	@Test
	public void TestTest() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/Test.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		List<Constraint> constraints;
		//First class
		ah.analyze(cs.get(0));
		constraints = ah.getTimeConstraint();


		assertTrue(check(
				86,
				"Thread.sleep(5000);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				205,
				"wait(100);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				232,
				"createTopologyThread.sleep(1000);",
				TimeInSignature.class,
				constraints
		));
		assertEquals(constraints.size(), 3);

		//Second class
		ah.analyze(cs.get(1));
		constraints = ah.getTimeConstraint();
		assertEquals(constraints.size(), 0);

		//Third class
		ah.analyze(cs.get(2));
		constraints = ah.getTimeConstraint();
		assertEquals(constraints.size(), 0);

		//Fourth class
		ah.analyze(cs.get(3));
		constraints = ah.getTimeConstraint();
		assertTrue(check(
				290,
				"Thread.sleep(5000);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				293,
				"Thread.sleep(5000);",
				TimeInSignature.class,
				constraints
		));
		assertTrue(check(
				302,
				"Thread.sleep(_class.SleepTimeout);",
				TimeInSignature.class,
				constraints
		));

	}

	@Test
	public void TesttestLambdas() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/testLambdas.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));
		List<Constraint> constraints = ah.getTimeConstraint();
		assertEquals(constraints.size(), 0);

	}

	@Test
	public void TestTimerEvent() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/TimerEvent.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();
		assertEquals(constraints.size(), 0);

	}

	@Test
	public void TestUPnPImpl() throws Exception {
		String filename = getClass().getClassLoader().getResource("examples/UPnPImpl.java").getFile();
		List<ASTClass> cs = init(filename);
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(TimeInSignature.class);
		ah.analyze(cs.get(0));

		List<Constraint> constraints = ah.getTimeConstraint();


		assertTrue(check(
				859,
				"socket.connect(new InetSocketAddress(control.getHost(), control.getPort()), CONNECT_TIMEOUT);",
				TimeInSignature.class,
				constraints
		));
		assertEquals(constraints.size(), 1);

	}

	private boolean check(int line, String code, Class _class, List<Constraint> constraints ){
		boolean flag = false;
		for(Constraint c : constraints){
			if(		c.getElm().getCode().equals(code) &&
					c.getLine() == line &&
					c.getCategory().equals(_class.getCanonicalName())){
				flag = true;
			}
		}
		return flag;
	}

}
