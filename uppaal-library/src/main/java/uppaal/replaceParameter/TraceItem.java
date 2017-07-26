package uppaal.replaceParameter;

/**
 * Created by giovanni on 03/05/2017.
 */
public class TraceItem {
    int threadID;
    String className;
    String methodName;
    int line;
    String varName;
    String varValue;

    public TraceItem(int threadID, String className, String methodName, int line, String varName, String varValue) {
        this.threadID = threadID;
        this.className = className;
        this.methodName = methodName;
        this.line = line;
        this.varName = varName;
        this.varValue = varValue;
    }

    public int getThreadID() {
        return threadID;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
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

    public String getVarValue() {
        return varValue;
    }

    public void setVarValue(String varValue) {
        this.varValue = varValue;
    }
}
