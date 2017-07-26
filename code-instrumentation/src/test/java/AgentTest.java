import instrumentation.Testing;
import org.junit.Test;

public class AgentTest {

    @Test
    public void shouldInstantiateSleepingInstance() throws InterruptedException {

        Testing sleeping = new Testing();
        sleeping.randomSleep(true);
    }

    @Test
    public void name() throws Exception {
        new UndefinedTimeBehaviour().method_1(100);

    }
}