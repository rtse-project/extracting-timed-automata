package automata;

import PCFG.converter.IConverter;
import PCFG.converter.ToUppaal;
import PCFG.creation.IM2CFG;
import PCFG.optimization.OptimizeTimeAutomata;
import PCFG.structure.PCFG;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by giovanni on 02/05/2017.
 */

public class ExtractModel {

    public static void main(String[] args) throws Exception {
        if(args.length != 4 && args.length != 5){
            System.err.println("Usage: filePath className methodName modelName projectPath");
            return;
        }

        String file = args[0];
        String className = args[1];
        String methodName = args[2];
        String modelName = args[3];
        if(!modelName.endsWith(".xml")){
            modelName = modelName + ".xml";
        }
        List<ASTClass> cs = JDTVisitor.parse( file, args.length == 5 ? args[4] : "/");

        System.out.println(">> Writing model: " + modelName);
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
        def(c,m, modelName);
    }

    public static void def(ASTClass c, IASTMethod m, String nameModel) throws Exception {

        IM2CFG p = new IM2CFG();
        p.addClass(c,m);
        PCFG graph = p.buildPCFG();
        graph.optimize();
        graph.optimize(new OptimizeTimeAutomata());

        BufferedWriter writer = null;
        //writer = new BufferedWriter(new FileWriter("graph.xal"));
        writer = new BufferedWriter(new FileWriter("./models/" + nameModel));
        IConverter toUppaal = new ToUppaal(c, ToUppaal.NAMING.LINE);
        //writer.write(toGraphViz.convert(graph));
        writer.write(toUppaal.convert(graph));
        writer.close();

    }
}
