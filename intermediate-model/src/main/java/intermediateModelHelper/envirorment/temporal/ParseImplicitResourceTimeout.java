package intermediateModelHelper.envirorment.temporal;

import intermediateModelHelper.envirorment.temporal.structure.ImplicitResourceTimeout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class ParseImplicitResourceTimeout extends ParseCSV {

    List<ImplicitResourceTimeout> methods = new ArrayList<>();

    public ParseImplicitResourceTimeout(String path) {
        super(path);
        super.start();
    }

    public ParseImplicitResourceTimeout(File file) {
        super(file);
        super.start();
    }

    public ParseImplicitResourceTimeout(InputStream stream) {
        super(stream);
        super.start();
    }


    public List<ImplicitResourceTimeout> getMethods(){
        return methods;
    }

    @Override
    protected void handleHeader(String[] header) {
        //we do not need headers
    }

    @Override
    protected void handleRow(String className, String methodName, String[] signature) {
        methods.add(new ImplicitResourceTimeout(className, methodName, Arrays.asList(signature)));
    }

}
