package automata;

import PCFG.converter.IConverter;
import PCFG.converter.ToUppaal;
import PCFG.creation.IM2CFG;
import PCFG.optimization.OptimizeTimeAutomata;
import PCFG.structure.PCFG;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.structure.RuntimeConstraint;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by giovanni on 02/05/2017.
 */

public class ExtractModelAndCnst {

    public static void main(String[] args) throws Exception {
        if(args.length != 6){
            System.err.println("Usage: filePath className methodName modelName constraintName projectPath");
            return;
        }

        String file = args[0];
        String className = args[1];
        String methodName = args[2];
        String modelName = args[3];
        String outputName = args[4];
        List<ASTClass> cs = JDTVisitor.parse( file, args[5]);

        System.out.println(">> Writing model: " + modelName);
        System.out.println(">> Writing conf file: " + outputName);
        ASTClass c = null;
        for(ASTClass cc : cs){
            if(cc.getName().equals(className))
                c = cc;
        }
        if(c == null) {
            System.err.println("Cannot found the correct class");
            System.exit(1);
        }
        IASTMethod m = c.getFirstMethodByName(methodName);
        def(c,m, modelName, outputName);
    }

    public static void def(ASTClass c, IASTMethod m, String nameModel, String nameConstraint) throws Exception {

        IM2CFG p = new IM2CFG();
        p.addClass(c,m);
        PCFG graph = p.buildPCFG();
        graph.optimize();
        graph.optimize(new OptimizeTimeAutomata());

        BufferedWriter writer = null;
        //writer = new BufferedWriter(new FileWriter("graph.xal"));
        writer = new BufferedWriter(new FileWriter(nameModel));
        IConverter toUppaal = new ToUppaal(c, ToUppaal.NAMING.LINE);
        //writer.write(toGraphViz.convert(graph));
        writer.write(toUppaal.convert(graph));
        writer.close();

        writer = new BufferedWriter(new FileWriter(nameConstraint));
        boolean first = true;
        for(RuntimeConstraint cnst : p.getResetRuntimeConstraints()){
            if(first){
                writer.write("class;method;line;var\n");
            }
            writer.write(cnst.toString() + "\n");
            writer.flush();
            first = false;
        }
        writer.close();
    }
}
