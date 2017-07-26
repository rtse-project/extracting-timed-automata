package instrumentation;

import instrumentation.data.Store;

import java.io.File;
import java.lang.instrument.Instrumentation;

/**
 * Created by giovanni on 27/04/2017.
 */
public class TestAgent {
    public static String filePath = System.getProperty("user.dir") +  File.separator + "traces" + System.currentTimeMillis() + ".txt";

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("AGENT INJECTED :: " + filePath);
        if(agentArgs != null){
            System.out.println("Args: " + agentArgs + "\n");
            String confPath = agentArgs;
            ReadConfFile readConfFile = new ReadConfFile(confPath);
            readConfFile.start();
            System.out.println("Classes: " + Store.getInstance().size() + " :: " + Store.getInstance().totalSize());
        }

        // registers the transformer
        inst.addTransformer(new TestInstrumentation());
    }
}
