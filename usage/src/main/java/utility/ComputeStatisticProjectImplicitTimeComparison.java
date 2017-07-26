package utility;

import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.heuristic.definition.ImplicitTimeComparisonApplication;
import intermediateModelHelper.indexing.IndexingProject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by giovanni on 31/05/2017.
 */
public class ComputeStatisticProjectImplicitTimeComparison {

    public ComputeStatisticProjectImplicitTimeComparison(String path, String name) throws IOException {
        Iterator<File> i = IndexingProject.getJavaFiles(path);
        int n_time_method = 0;

        System.out.println("Project: " + name);
        long start = System.currentTimeMillis();
        while (i.hasNext()) {
            String filename = i.next().getAbsolutePath();
            List<ASTClass> result = JDTVisitor.parse(filename, "/Users/giovanni/Documents/toread/Reading");

            for(ASTClass c : result){
                //compute time
                ApplyHeuristics ah = new ApplyHeuristics();
                ah.set__DEBUG__(false);
                ah.subscribe(ImplicitTimeComparisonApplication.class);
                ah.analyze(c);
                List<Constraint> l = ah.getTimeConstraint();
                n_time_method += l.size();
            }
        }
        System.out.println("#Implicit Time Comparison: " + n_time_method);
        long end = System.currentTimeMillis() - start;
        System.out.println("It took " + end + "ms");

    }

    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.out.println("Usage with: project_path project_name");
            System.exit(1);
        }
        String path = args[0];
        String name = args[1];
        new ComputeStatisticProjectImplicitTimeComparison(path, name);
    }
}
