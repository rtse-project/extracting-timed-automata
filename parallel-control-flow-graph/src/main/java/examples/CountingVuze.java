package examples;


import PCFG.converter.IConverter;
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
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
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
public class CountingVuze {

	String project_name = "vuze_testing";

	public static void main(String[] args) throws Exception {
		CountingVuze m = new CountingVuze();
		//m.run(args[0], args[1]);
		String base_vuze = "/Users/giovanni/repository/sources/vuze/src/main/java/";
		//String working = "/Users/giovanni/repository/sources/vuze/src/main/java/";
		String working = "/Users/giovanni/repository/java-xal/PCFG/src/main/resources/VuzeExample.java";
		String out = "/Users/giovanni/repository/java-xal/";
		m.printExample(base_vuze, working, out);
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

	public void printExample(String base, String working, String outputdir) throws IOException {
		this.loadUserType();
		List<ASTClass> classes = JDTVisitor.parse(working, base);
		ASTClass c = classes.get(0);
		IM2CFG p = new IM2CFG();
		p.addClass(c, c.getMethods().get(0));
		PCFG graph = p.buildPCFG();
		graph.optimize();
		graph.optimize(new OptimizeTimeAutomata());

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputdir + "graph.xal"));
		IConverter toGraphViz = new ToXAL(c);
		writer.write(toGraphViz.convert(graph));
		writer.close();
	}


	public void run(String base, String working, String outputdir)  throws Exception{
		if(!outputdir.endsWith("/")){
			outputdir += "/";
		}
		List<String> files = new ArrayList<>();
		{
			Iterator<File> f = IndexingProject.getJavaFiles(working);
			while (f.hasNext()) {
				files.add(f.next().getAbsolutePath());
			}
		}
		/*
		IndexingProject indexingProject = new IndexingProject(this.project_name);
		indexingProject.setSkipTest(false);
		indexingProject.setShowUpdates(true);
		indexingProject.indexProject(working, true);
		System.out.println("> Indexing Complete <");
		*/

		this.loadUserType();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputdir + "vuze_counting.csv"));
		writer.write("file;class;method;signature;count\n");
		int current = 0;
		int size = files.size();
		for(String f : files) {
			System.out.print("\r                                                 ");
			System.out.print(String.format("\r[DEBUG] Processing %f %%", (double)current/(double)size));
			//if(!f.equals("/Users/giovanni/repository/sources/vuze/src/main/java/com/aelitis/azureus/core/diskmanager/cache/impl/CacheFileWithCache.java"))
			//	continue;
			List<ASTClass> classes = JDTVisitor.parse(f, base);
			for(ASTClass c : classes) {
				for(IASTMethod m : c.getMethods()) {
					IM2CFG p = new IM2CFG();
					p.addClass(c, m);
					if(p.getConstraintsSize() == 0){
						continue;
					}
					String filename = c.getPath();
					String className = c.getName();
					String methodName = m.getName();
					String sign = m.getSignature().stream().reduce("", String::concat);
					String qualified = String.format("%s;%s;%s;%s;%d\n", filename, className, methodName, sign, p.getConstraintsSize());
					writer.write(qualified);
					writer.flush();

				}
			}
		}
		System.out.println("\n\n> Processing Complete <");
		writer.close();
	}




}
