package examples;


import PCFG.converter.IConverter;
import PCFG.converter.ToDot;
import PCFG.converter.ToUppaal;
import PCFG.converter.ToXAL;
import PCFG.creation.IM2CFG;
import PCFG.creation.IM2PCFG;
import PCFG.optimization.OptimizeTimeAutomata;
import PCFG.structure.PCFG;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import parser.Java2AST;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class GeneralGarbageMain {

	List<ASTClass> classes = new ArrayList<>();
	static final String db_name = "jetty";

	public static void main(String[] args) throws Exception {
		MongoOptions.getInstance().setDbName(db_name);
		GeneralGarbageMain m = new GeneralGarbageMain();
		m.run_wait();
		if(true)
			return;
		if(args.length > 0)
			m.example_paper(args);
		else
			m.run("/Users/giovanni/repository/java-xal/PCFG/src/test/resources/fogassistant2");
	}

	private void run_wait() throws Exception {
		String f =  "/Users/giovanni/repository/java-xal/PCFG/src/test/resources/time/UndefiniteTimeBehaviour.java";
		ASTClass c = JDTVisitor.parse(f,System.getProperty("user.dir")).get(0);
		IASTMethod m = c.getAllMethods().get(0);
		IM2CFG p = new IM2CFG();
		p.addClass(c,m);
		PCFG graph = p.buildPCFG();
		graph.optimize();
		graph.optimize(new OptimizeTimeAutomata());

		BufferedWriter writer = null;
		//writer = new BufferedWriter(new FileWriter("graph.xal"));
		writer = new BufferedWriter(new FileWriter("graph.xml"));
		IConverter toGraphViz = new ToXAL(c);
		IConverter toUppaal = new ToUppaal(ToUppaal.NAMING.PRETTY);
		//writer.write(toGraphViz.convert(graph));
		writer.write(toUppaal.convert(graph));
		writer.close();
	}


	public void run() throws Exception {
		String f =  GeneralGarbageMain.class.getClassLoader().getResource("Thread_1.java").getFile();
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		classes.addAll(v.listOfClasses);
		ASTClass c = classes.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c);
		PCFG graph = p.buildPCFG();
		graph.optimize();

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter("graph.xal"));
		IConverter toGraphViz = new ToXAL(c);
		writer.write(toGraphViz.convert(graph));
		writer.close();
	}

	public void run1() throws Exception {
		String f =  "/Users/giovanni/repository/sources/jetty/jetty-websocket/javax-websocket-client-impl/src/main/java/org/eclipse/jetty/websocket/jsr356/ClientContainer.java";
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		classes.addAll(v.listOfClasses);
		ASTClass c = classes.get(0);

		//add the second method
		f =  "/Users/giovanni/repository/sources/jetty/jetty-websocket/websocket-client/src/main/java/org/eclipse/jetty/websocket/client/WebSocketClient.java";
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c);
		p.addClass(c1);
		PCFG graph = p.buildPCFG();
		graph.optimize();

		//BufferedWriter writer = null;
		//writer = new BufferedWriter(new FileWriter("graph.dot"));
		//IConverter toGraphViz = new ToDot(true);
		//writer.write(toGraphViz.convert(graph));
		//writer.close();
	}

	public void example_paper(String[] args) throws Exception {
		MongoOptions.getInstance().setDbName("vuze");
		String f =  "/Users/giovanni/repository/sources/vuze/src/main/java/com/aelitis/azureus/core/impl/AzureusCoreImpl.java";
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		classes.addAll(v.listOfClasses);
		ASTClass c = classes.get(0);

		//add the second method
		f =  "/Users/giovanni/repository/sources/vuze/src/main/java/com/aelitis/azureus/ui/common/table/impl/TableViewImpl.java";
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		IM2PCFG p = new IM2PCFG();
		p.addClass(c, c.getFirstMethodByName("canStart"));
		p.addClass(c1, c1.getFirstMethodByName("runForSelectedRows"));
		PCFG graph = p.buildPCFG();
		graph.optimize();

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter("graph_example_1.dot"));
		IConverter toGraphViz = new ToDot(true);
		writer.write(toGraphViz.convert(graph));
		writer.close();

		//MongoOptions.getInstance().setDbName(db_name);
		String folder = args[0];
		new GeneralGarbageMain().run(folder);
	}


	public void run(String path) throws Exception {
		String _NAME_ = path.substring( path.lastIndexOf("/") +1 );
		MongoOptions.getInstance().setDbName(_NAME_);
		double start = new Date().getTime();
		IndexingProject indexing = new IndexingProject(_NAME_);
		indexing.delete();
		System.out.println("[Indexing] Started");
		indexing.indexProject(path, false);
		indexing.indexSyncBlock(path, false);
		indexing.indexSyncCall(path, false);
		double end = new Date().getTime();
		System.out.println("[Indexing] Finish: "+ (end-start)/1000 + " s");

		start = new Date().getTime();
		System.out.println("[XAL] Creation Started");

		File dir = new File(path);
		String[] filter = {"java"};
		Collection<File> files = FileUtils.listFiles(
				dir,
				filter,
				true
		);
		Iterator i = files.iterator();
		while (i.hasNext()) {
			String filename = ((File)i.next()).getAbsolutePath();
			System.out.println("[DEBUG] Processing file: " + filename);
			String fName = filename.substring( filename.lastIndexOf("/") +1 );
			fName = fName.substring(0 , fName.indexOf("."));
			//pp filename
			for(ASTClass c : JDTVisitor.parse(filename)){
				if(c.getMethods().size() == 0) continue;
				IM2PCFG p = new IM2PCFG();
				p.addClass(c);
				// build
				PCFG graph = p.buildPCFG();
				BufferedWriter writer = null;
				//if(v.listOfClasses.size() > 1)
				writer = new BufferedWriter(new FileWriter( c.getPackageName().replace(".","_") + "_" + c.getName() + ".xal"));
				//else
				//	writer = new BufferedWriter(new FileWriter( c.getName() + ".xal"));
				IConverter toGraphViz = new ToXAL(c);
				writer.write(toGraphViz.convert(graph));
				writer.close();
			}
		}
		end = new Date().getTime();
		System.out.println("[XAL] Creation Ended: " + (end-start)/1000 + " s");

	}

	public void example_paper2() throws Exception {
		MongoOptions.getInstance().setDbName("jetty");
		String f =  "/Users/giovanni/repository/sources/jetty/jetty-websocket/javax-websocket-client-impl/src/main/java/org/eclipse/jetty/websocket/jsr356/ClientContainer.java";
		Java2AST a = new Java2AST(f, true);
		CompilationUnit ast = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(ast, f);
		ast.accept(v);
		classes.addAll(v.listOfClasses);
		ASTClass c = classes.get(0);

		//add the second method
		f =  "/Users/giovanni/repository/sources/jetty/jetty-websocket/websocket-client/src/main/java/org/eclipse/jetty/websocket/client/WebSocketClient.java";
		a = new Java2AST(f, true);
		ast = a.getContextJDT();
		v = new JDTVisitor(ast, f);
		ast.accept(v);
		ASTClass c1 = v.listOfClasses.get(0);

		int max = 0;
		PCFG g = null;
		for(IASTMethod m0 : c.getAllMethods()){
			for(IASTMethod m1 : c1.getAllMethods()){
				IM2PCFG p = new IM2PCFG();
				p.addClass(c, m0);
				p.addClass(c1, m1);
				PCFG graph = p.buildPCFG();
				if(graph.getESync().size() > max){
					g = graph;
					max = graph.getESync().size();
				}
			}
		}


		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter("graph_example_2.dot"));
		IConverter toGraphViz = new ToDot(true);
		writer.write(toGraphViz.convert(g));
		writer.close();
	}


}
