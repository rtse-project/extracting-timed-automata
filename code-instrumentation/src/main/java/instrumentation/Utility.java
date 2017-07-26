package instrumentation;

/**
 * Created by giovanni on 28/04/2017.
 */
public class Utility {
    public static String convertJavaToPkgName(String className){
        return className.replace("/",".");
    }
    public static String convertPkgNametoJava(String className){
        return className.replace(".","/");
    }
}
