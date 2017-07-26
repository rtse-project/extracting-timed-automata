package examples;


import PCFG.converter.IConverter;
import PCFG.converter.ToUppaal;
import PCFG.converter.ToXAL;
import PCFG.creation.IM2CFG;
import PCFG.optimization.OptimizeTimeAutomata;
import PCFG.structure.PCFG;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeTypes;
import intermediateModelHelper.indexing.IndexingFile;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.structure.IndexData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class Kafka_3540 {

	String project_name = "kafka-3540";

	public static void main(String[] args) throws Exception {
		Kafka_3540 m = new Kafka_3540();
		String output = args.length > 2 ? args[2] : System.getProperty("user.dir");
		m.run(args[0], args[0], output, args[1]);
	}

	public void loadUserType(){
		MongoConnector mongo = MongoConnector.getInstance(project_name);
		List<IndexData> data = mongo.getTimedMethod();
		List<TimeTypes> usertypes = new ArrayList<>();
		for(IndexData d : data){
			String name = d.getFullName();
			for(IndexData.IndexTimeMethod method : d.getListOfTimedMethods()){
				usertypes.add(
						new TimeTypes(name, method.getName(), method.getSignature())
				);
			}
			//System.out.println(name);
		}
		TemporalInfo.getInstance().addTimeTypes(usertypes);
		System.out.println(">Loaded " + data.size() + " User Types<");
	}


	public void run(String base, String working, String outputdir, String outputFile)  throws Exception{
		if(!outputdir.endsWith("/") && !outputFile.startsWith("/")){
			outputdir += "/";
		}
		List<String> files = new ArrayList<>();
		{
			Iterator<File> f = IndexingProject.getJavaFiles(working);
			while (f.hasNext()) {
				files.add(f.next().getAbsolutePath());
			}
		}


		//IndexingProject indexingProject = new IndexingProject(this.project_name);
		//indexingProject.setSkipTest(false);
		//indexingProject.setShowUpdates(true);
		//indexingProject.indexProject(working, true);
		//System.out.println("> Indexing Complete <");

		//this.loadUserType();

		String f = base + "org/apache/kafka/clients/consumer/internals/ConsumerCoordinator.java";
		System.out.println("> Processing ConsumerCoordinator.java<");
		List<ASTClass> classes = JDTVisitor.parse(f, base);
		ASTClass c = classes.get(0);
		IASTMethod m = c.getFirstMethodByName("commitOffsetsSync");
		IM2CFG p = new IM2CFG();
		p.addClass(c, m);
		//p.addClass(c, c.getFirstMethodByName("interruptClusterStateProcessing"));
		PCFG graph = p.buildPCFG();
		graph.optimize();
		graph.optimize(new OptimizeTimeAutomata());

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputdir + outputFile));
		IConverter toGraphViz = new ToUppaal(c, ToUppaal.NAMING.LINE);
		writer.write(toGraphViz.convert(graph));
		writer.close();

		f = base + "org/apache/kafka/clients/consumer/internals/AbstractCoordinator.java";
		System.out.println("> Processing ConsumerCoordinator.java<");
		classes = JDTVisitor.parse(f, base);
		c = classes.get(0);
		m = c.getFirstMethodByName("ensureCoordinatorReady");
		p = new IM2CFG();
		p.addClass(c, m);
		//p.addClass(c, c.getFirstMethodByName("interruptClusterStateProcessing"));
		graph = p.buildPCFG();
		graph.optimize();
		graph.optimize(new OptimizeTimeAutomata());

		writer = null;
		writer = new BufferedWriter(new FileWriter(outputdir + "2_" + outputFile));
		toGraphViz = new ToUppaal(ToUppaal.NAMING.LINE);
		writer.write(toGraphViz.convert(graph));
		writer.close();

		System.out.println("\n> Processing Complete <");

	}




}
