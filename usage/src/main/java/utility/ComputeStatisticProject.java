package utility;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.heuristic.definition.TimeApplication;
import intermediateModelHelper.indexing.IndexingProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by giovanni on 31/05/2017.
 */
public class ComputeStatisticProject {

    public ComputeStatisticProject(String path, String name) throws IOException {
        Iterator<File> i = IndexingProject.getJavaFiles(path);
        int n_file = 0;
        int n_class = 0;
        int n_method = 0;
        int n_time_method = 0;

        System.out.println("Project: " + name);
        BufferedWriter writer = null;
        //writer = new BufferedWriter(new FileWriter("graph.xal"));
        writer = new BufferedWriter(new FileWriter(name + ".csv"));
        writer.write("class;method;signature\n");
        while (i.hasNext()) {
            String filename = i.next().getAbsolutePath();
            List<ASTClass> result = JDTVisitor.parse(filename, path);
            for(ASTClass c : result){
                n_class++;
                n_method += c.getMethods().size();
                //compute time
                ApplyHeuristics ah = new ApplyHeuristics();
                ah.set__DEBUG__(false);
                ah.subscribe(TimeApplication.class);
                ah.analyze(c);
                for(IASTMethod m : c.getMethods()){
                    if(m.hasTimeCnst()){
                        n_time_method++;
                        String line = String.format("%s;%s;%s\n", c.toString(),m.getName(), Arrays.toString(m.getParameters().toArray()) );
                        writer.write(line);
                        writer.flush();
                    }
                }
            }
            n_file++;
        }
        writer.close();
        System.out.println("#File:        " + n_file);
        System.out.println("#Class:       " + n_class);
        System.out.println("#Method:      " + n_method);
        System.out.println("#Time Method: " + n_time_method);

    }

    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.out.println("Usage with: project_path project_name");
            System.exit(1);
        }
        String path = args[0];
        String name = args[1];
        new ComputeStatisticProject(path, name);
    }
}
