package instrumentation.data;

/**
 * Created by giovanni on 28/04/2017.
 */
public class StoreItem {
    String javaClassName;
    String className;
    String methodName;
    int line;
    String varName;

    public StoreItem() {
    }

    public StoreItem(String javaClassName, String className, String methodName, int line, String varName) {
        this.javaClassName = javaClassName;
        this.className = className;
        this.methodName = methodName;
        this.line = line;
        this.varName = varName;
    }

    public String getJavaClassName() {
        return javaClassName;
    }

    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return String.format("%s.%s %s@%d",  className, methodName, varName, line);
    }
}
