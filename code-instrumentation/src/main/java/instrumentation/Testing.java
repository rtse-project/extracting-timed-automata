package instrumentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giovanni on 27/04/2017.
 */
public class Testing {
    public void randomSleep(boolean f) throws InterruptedException {

        Map n = new HashMap();
        if(f) return;
        // randomly sleeps between 500ms and 1200s
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        System.out.printf("Sleeping for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);
        n.put("t", randomSleepDuration);
        long abc = 10;
    }
}
