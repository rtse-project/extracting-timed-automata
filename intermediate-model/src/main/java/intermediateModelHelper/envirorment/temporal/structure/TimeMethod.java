package intermediateModelHelper.envirorment.temporal.structure;

import java.util.List;

/**
 * Created by giovanni on 07/03/2017.
 */
public class TimeMethod extends TimeInfo {

    private int[] timeouts;

    public TimeMethod(String className, String methodName, List<String> signature, int[] timeouts) {
        super(className, methodName, signature);
        this.timeouts = timeouts;
    }

    public int[] getTimeouts() {
        return timeouts;
    }
}
