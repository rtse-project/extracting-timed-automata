package mains;

import intermediateModel.Utils;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.heuristic.definition.SetTimeoutPermissive;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class MainOnlySetTimeout {


	public static void main(String[] args) throws Exception {



		String name = args.length > 0 ? args[0] : "output";
		name = name.replace(".", "_");
		String base_path = args.length > 0 ? args[1] :"/Users/giovanni/repository/java-xal/project_eval/";
		String csvFile = name + "_timeoutPermissive.csv";
		System.out.println("[DEBUG] Project Name: " + name);
		System.out.println("[DEBUG] Project Path: " + base_path);

		System.out.println("[DEBUG] Start indexing.");
		MongoOptions.getInstance().setDbName(name);
		IndexingProject indexingProject = new IndexingProject(name);
		indexingProject.indexProject(base_path, false);
		System.out.println("[DEBUG] Finish indexing.");
		System.out.println("[DEBUG] Computing time constraint.");

		FileWriter writer = new FileWriter(csvFile);
		writer.write("path;implicit_timeout;set_timeout;expired_resource\n");
		writer.flush();
		List<String> files = new ArrayList<>();
		{
			Iterator<File> f = IndexingProject.getJavaFiles(base_path);
			while (f.hasNext()) {
				files.add(f.next().getAbsolutePath());
			}
		}
		System.out.println("#files " + files.size());
		int counter = 0;
		//files.add(args[0]);
		for(int i = 0; i < files.size(); i ++){
			String f = files.get(i);
			List<ASTClass> lists = JDTVisitor.parse(f, base_path);
			int implicit_timeout = 0;
			int set_timeout = 0;
			int resource_expired = 0;
			//List<ASTClass> lists = JDTVisitor.parse(f,"/Users/giovanni/repository/java-xal/project_eval/activemq/" );
			for(ASTClass c : lists){

				ApplyHeuristics ah = new ApplyHeuristics();
				ah.set__DEBUG__(false);
				ah.subscribe(SetTimeoutPermissive.class);
				ah.analyze(c);
				List<Constraint> cnst = ah.getTimeConstraint();
				if(cnst.size() > 0){
					for(Constraint t : cnst){
						String type = t.getCategory();
						if(type.equals(SetTimeoutPermissive.class.getCanonicalName())){
							set_timeout++;
						}
					}

				}
			}
			if((implicit_timeout+set_timeout+resource_expired) > 0) {
				String line = String.format("%s;%d;%d;%d\n", f, implicit_timeout, set_timeout, resource_expired);
				//System.out.println(line);
				writer.write(line);
				writer.flush();
			}
		}
		writer.flush();
		writer.close();
		Utils.send_email(String.format("Project %s {%s} ends.\r\n", name, base_path));
		System.out.println("#Skipped " + counter);
		System.out.println("[DEBUG] Finish project " + name);
		MongoConnector.getInstance(name).close();
	}
}
