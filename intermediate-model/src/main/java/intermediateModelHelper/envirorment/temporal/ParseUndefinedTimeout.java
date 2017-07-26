package intermediateModelHelper.envirorment.temporal;

import intermediateModelHelper.envirorment.temporal.structure.TimeUndefinedTimeout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class ParseUndefinedTimeout extends ParseCSV {

    List<TimeUndefinedTimeout> methods = new ArrayList<>();

    public ParseUndefinedTimeout(String path) {
        super(path);
        super.start();
    }

    public ParseUndefinedTimeout(File file) {
        super(file);
        super.start();
    }

    public ParseUndefinedTimeout(InputStream stream) {
        super(stream);
        super.start();
    }


    public List<TimeUndefinedTimeout> getMethods(){
        return methods;
    }

    @Override
    protected void handleHeader(String[] header) {
        //we do not need headers
    }

    @Override
    protected void handleRow(String className, String methodName, String[] signature) {
        methods.add(new TimeUndefinedTimeout(className, methodName, Arrays.asList(signature)));
    }

}
