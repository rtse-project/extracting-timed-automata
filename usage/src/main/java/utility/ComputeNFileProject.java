package utility;

import intermediateModelHelper.indexing.IndexingProject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by giovanni on 31/05/2017.
 */
public class ComputeNFileProject {

    public ComputeNFileProject(String name, String path) throws IOException {
        Iterator<File> i = IndexingProject.getJavaFiles(path);
        int n_file = 0;
        int n_class = 0;
        int n_method = 0;

        System.out.println("Project: " + name);
        while (i.hasNext()) {
            String filename = i.next().getAbsolutePath();
            //List<ASTClass> result = JDTVisitor.parseSpecial(filename, path);
            //for(ASTClass c : result){
              //  n_class++;
                //n_method += c.getMethods().size();
                //compute time
            //
            // }
            n_file++;
        }
        System.out.println("#File:        " + n_file);
        System.out.println("#Class:       " + n_class);
        System.out.println("#Method:      " + n_method);

    }

    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.out.println("Usage with: project_path project_name");
            System.exit(1);
        }
        String path = args[0];
        String name = args[1];
        new ComputeNFileProject(path, name);
    }
}
