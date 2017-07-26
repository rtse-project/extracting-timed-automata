package mains;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTDoWhile;
import intermediateModel.structure.ASTWhile;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.indexing.IndexingProject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class FindGoodCandidateForManualEvaluation {

	public static void main(String[] args) throws Exception {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		List<String> files = new ArrayList<>();
		{
			Iterator<File> f = IndexingProject.getJavaFiles(args[0]);
			while (f.hasNext()) {
				files.add(f.next().getAbsolutePath());
			}
		}
		double tot = files.size();
		FileWriter writer = new FileWriter(args[1] + "goodMethod.csv");
		writer.write("path;line\n");
		writer.flush();
		double current = 0.0;
		String space = "\r                                                 ";
		for(String file : files){
			current++;
			System.out.print(String.format("%s\r[DEBUG] Processing %f %%", space, current/tot*100));
			List<ASTClass> cs = JDTVisitor.parse(file, args[0]);
			for(ASTClass c : cs){
				List<Constraint> cnst = ApplyHeuristics.getConstraint(c);
				List<IASTMethod> methodWithWhile = new ArrayList<>();
				for(IASTMethod m : c.getMethods()){
					m.visit(new DefaultASTVisitor(){
						@Override
						public void enterASTDoWhile(ASTDoWhile elm) {
							methodWithWhile.add(m);
						}

						@Override
						public void enterASTWhile(ASTWhile elm) {
							methodWithWhile.add(m);
						}
					});
				}
				for(IASTMethod m : methodWithWhile){
					int startLine =  m.getLine();
					int endLine   =  m.getLineEnd();
					for(Constraint time : cnst){
						int line = time.getLine();
						if(startLine <= line && line <= endLine){
							writer.write(String.format("%s;%d\n",file,startLine));
							writer.flush();
						}
					}
				}
			}
		}
		writer.close();
	}
}
