package intermediateModelHelper.envirorment.temporal;

import intermediateModelHelper.envirorment.temporal.structure.TimeTypes;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class ParseTypes extends ParseCSV {

    List<TimeTypes> methods = new ArrayList<>();

    public ParseTypes(String path) {
        super(path);
        super.setSplitSignature(true);
        super.start();
    }

    public ParseTypes(File file) {
        super(file);
        super.setSplitSignature(true);
        super.start();
    }

    public ParseTypes(InputStream stream) {
        super(stream);
        super.setSplitSignature(true);
        super.start();
    }

    public List<TimeTypes> getMethods(){
        return methods;
    }

    @Override
    protected void handleHeader(String[] header) {
        //we do not need headers
    }

    @Override
    protected void handleRow(String className, String methodName, String[] signature) {
        methods.add(new TimeTypes(className, methodName, Arrays.asList(signature)));
    }

}
