package intermediateModelHelper.envirorment.temporal.structure;

import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public abstract class TimeInfo {
    String className;
    String methodName;
    List<String> signature;

    public TimeInfo(String className, String methodName, List<String> signature) {
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
    }

    public TimeInfo() {
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

    public List<String> getSignature() {
        return signature;
    }

    public void setSignature(List<String> signature) {
        this.signature = signature;
    }
}
