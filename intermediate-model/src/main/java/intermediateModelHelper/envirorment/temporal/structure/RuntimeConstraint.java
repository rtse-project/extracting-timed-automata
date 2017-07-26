package intermediateModelHelper.envirorment.temporal.structure;

/**
 * Created by giovanni on 22/03/2017.
 */
public class RuntimeConstraint {
    String className;
    String methodName;
    int line;
    String varName;

    public RuntimeConstraint(String className, String methodName, int line, String varName) {
        this.className = className;
        this.methodName = methodName;
        this.line = line;
        this.varName = varName;
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
        return String.format("%s;%s;%d;%s", className, methodName, line, varName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuntimeConstraint that = (RuntimeConstraint) o;

        if (line != that.line) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        return varName != null ? varName.equals(that.varName) : that.varName == null;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + line;
        result = 31 * result + (varName != null ? varName.hashCode() : 0);
        return result;
    }
}
