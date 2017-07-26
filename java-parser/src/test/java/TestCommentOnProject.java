import org.apache.commons.io.FileUtils;
import parser.Java2AST;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by giovanni (@thisthatDC) on 18/03/16.
 */
public class TestCommentOnProject {

    static int nException = 0;
    static List<String> errors = new ArrayList<String>();
    static int line_number = 0;
    BufferedWriter out;


    //@Test
    public void testAll() throws Exception {

        String lastOne = "/Users/giovanni/repository/java-parser/testProject/spark-1.6.1/unsafe/src/testenvirorments.test_antrl/java/org/apache/spark/unsafe/types/UTF8StringSuite.java";
        //Log to file
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        File file = new File("errors-" + dateFormat.format(date) + ".log") ;
        out = new BufferedWriter(new FileWriter(file));


        String base_path = System.getProperty("user.dir");
        base_path += "/testProject";
        File dir = new File(base_path);
        String[] filter = {"java"};
        Collection<File> files = FileUtils.listFiles(
                dir,
                filter,
                true
        );
        Iterator i = files.iterator();
        boolean reached = false;
        while (i.hasNext()) {
            String filename = ((File)i.next()).getAbsolutePath();
            if(!reached && !filename.equals(lastOne))
                continue;
            reached = true;
            try {
                Java2AST a = new Java2AST(filename, true);
                a.getContextJDT();
                String s = "Correctly parsed:" + filename;
                System.out.println(s);
            } catch (Exception e) {
                //e.printStackTrace();
                nException++;
            }
        }

        if(nException > 0)
            throw new Exception("Test phase not passed! " + nException + " errors.");
        System.err.println(Arrays.toString(errors.toArray()));
        out.close();
    }


    private void walk( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            }
            else {
                String filename = f.getAbsoluteFile().toString();
                if(getExt(filename).equals("java")){
                    //java file, let's testenvirorments.test_antrl it!
                    try {
                        Java2AST a = new Java2AST(filename, true);
                        a.getContextJDT();
                        String s = "Correctly parsed:" + filename;
                        System.out.println(s);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        nException++;
                    }
                }
            }
        }
    }

    private String getExt(String file){
        String tmp = file.substring(file.lastIndexOf(".") + 1);
        //System.out.println(tmp + " :: " + file);
        return tmp;
    }


}
