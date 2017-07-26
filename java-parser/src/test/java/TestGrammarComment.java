import org.junit.Test;
import parser.Java2AST;

/**
 * Created by giovanni (@thisthatDC) on 18/03/16.
 */
public class TestGrammarComment {

    public void testGrammarByName(String name) throws Exception {
        String f =  TestGrammarComment.class.getClassLoader().getResource(name).getFile();
        Java2AST a = new Java2AST(f, true);
        a.getContextJDT();
        //System.err.println("____________");
    }

    @Test
    public void testExampleField() throws Exception {
        testGrammarByName("HelloWorld.java");
    }

    @Test
    public void testAntlr4Mojo() throws Exception {
        testGrammarByName("Antlr4Mojo.java");
    }

    @Test
    public void testCalculator() throws Exception {
        testGrammarByName("Calculator.java");
    }

    @Test
    public void testLocalDiscovery() throws Exception {
        testGrammarByName("LocalDiscovery.java");
    }

    //@Test(expected=Exception.class)
	@Test
    public void testOnlyMethod() throws Exception {
        testGrammarByName("OnlyMethod.java");
    }

    @Test
    public void testPredictionModule() throws Exception {
        testGrammarByName("PredictionModule.java");
    }

    @Test
    public void testSchedulerImpl() throws Exception {
        testGrammarByName("SchedulerImpl.java");
    }

}
