package intermediateModelHelper.envirorment.temporal;

import intermediateModelHelper.envirorment.temporal.structure.TimeMethod;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class ParseMethods extends ParseCSV {

    List<TimeMethod> methods = new ArrayList<>();

    public ParseMethods(String path) {
        super(path);
        super.start();
    }

    public ParseMethods(File file) {
        super(file);
        super.start();
    }

    public ParseMethods(InputStream stream) {
        super(stream);
        super.start();
    }

    @Override
    protected void start(InputStreamReader file){
        boolean notHeader = false;
        try (BufferedReader br = new BufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] row = line.split(separator);
                if(notHeader) {
                    if(row.length == 4){
                        handleRow(row[0],row[1], row[2].split(","), Arrays.stream(row[3].split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray());
                    } else {
                        handleRow(row[0],row[1], new String[0]);
                    }

                }
                else {
                    notHeader = true;
                    handleHeader(row);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }


    public List<TimeMethod> getMethods(){
        return methods;
    }

    @Override
    protected void handleHeader(String[] header) {
        //we do not need headers
    }

    @Override
    protected void handleRow(String className, String methodName, String[] signature) {

    }

    protected void handleRow(String className, String methodName, String[] signature, int[] timeouts) {
        List<String> sign = Arrays.asList(signature);
        this.methods.add(new TimeMethod(className, methodName, sign, timeouts));
    }

}
