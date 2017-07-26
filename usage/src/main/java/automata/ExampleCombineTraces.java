package automata;

import PCFG.converter.IConverter;
import PCFG.converter.ToUppaal;
import PCFG.converter.ToXAL;
import PCFG.creation.IM2CFG;
import PCFG.optimization.OptimizeTimeAutomata;
import PCFG.structure.PCFG;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import uppaal.replaceParameter.HandleReplacement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by giovanni on 03/05/2017.
 *
 * KAKFA-3540
 *  -issue:
/Users/giovanni/Documents/research/timing/issue_time/kafka_3540/instrumentation/issue/kafka_3540.xml
/Users/giovanni/Documents/research/timing/issue_time/kafka_3540/instrumentation/issue/traces.txt
/Users/giovanni/Documents/research/timing/issue_time/kafka_3540/instrumentation/issue/
 *
 * -fix:
/Users/giovanni/Documents/research/timing/issue_time/kafka_3540/instrumentation/fix/kafka_3540_fix.xml
/Users/giovanni/Documents/research/timing/issue_time/kafka_3540/instrumentation/fix/traces1494330570156.txt
/Users/giovanni/Documents/research/timing/issue_time/kafka_3540/instrumentation/fix/
 *
 */
public class ExampleCombineTraces {
    public static void main(String[] args) throws IOException {
        if(args.length == 0)
            def();
        else
            HandleReplacement.createModelsFromTraces(args[0], args[1], args[2]);
    }

    private static void def() throws IOException {
        String f =  ExampleCombineTraces.class.getClassLoader().getResource("exampleTraces/UndefinedTimeBehaviour.java").getFile();
        String base = f.substring(0, f.lastIndexOf("/") + 1);
        ASTClass c = JDTVisitor.parse(f,base).get(0);
        IASTMethod m = c.getAllMethods().get(0);
        IM2CFG p = new IM2CFG();
        p.addClass(c,m);
        PCFG graph = p.buildPCFG();
        graph.optimize();
        graph.optimize(new OptimizeTimeAutomata());
        BufferedWriter writer = null;
        //writer = new BufferedWriter(new FileWriter("graph.xal"));
        writer = new BufferedWriter(new FileWriter(base + "base_graph.xml"));
        IConverter toGraphViz = new ToXAL(c);
        IConverter toUppaal = new ToUppaal(ToUppaal.NAMING.LINE);
        //writer.write(toGraphViz.convert(graph));
        writer.write(toUppaal.convert(graph));
        writer.close();
        HandleReplacement.createModelsFromTraces(base + "base_graph.xml", base + "traces.txt", base);
    }
}
