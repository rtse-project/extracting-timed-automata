package intermediateModelHelper.envirorment.temporal;

import intermediateModelHelper.envirorment.temporal.structure.TimeTimeout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class ParseTimeout extends ParseCSV {

    List<TimeTimeout> methods = new ArrayList<>();

    public ParseTimeout(String path) {
        super(path);
        super.start();
    }

    public ParseTimeout(File file) {
        super(file);
        super.start();
    }

    public ParseTimeout(InputStream stream) {
        super(stream);
        super.start();
    }


    public List<TimeTimeout> getMethods(){
        return methods;
    }

    @Override
    protected void handleHeader(String[] header) {
        //we do not need headers
    }

    @Override
    protected void handleRow(String className, String methodName, String[] signature) {
        methods.add(new TimeTimeout(className, methodName, Arrays.asList(signature)));
    }

}
