package intermediateModelHelper.envirorment.temporal;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.*;
import intermediateModel.visitors.ExtractTimeAttribute;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.structure.TimeTypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectReturnTimeMethods extends ParseIM {
    static {
        new File("config").mkdir();
    }

    boolean store = false;
    String storeName = "";
    List<TimeTypes> output;
    IASTMethod lastMethod = null;

    public CollectReturnTimeMethods(ASTClass _class, boolean store, String storeName) {
        super(_class);
        this.store = store;
        this.storeName = storeName;
    }

    public CollectReturnTimeMethods(boolean store, String storeName) {
        this.store = store;
        this.storeName = storeName;
    }

    @Override
    protected void analyzeMethod(IASTMethod method, Env e) {
        IASTMethod bck = lastMethod;
        lastMethod = method;
        super.analyzeMethod(method, e);
        lastMethod = bck;
    }

    public List<TimeTypes> index(ASTClass c){
        output = new ArrayList<>();
        super.set_class(c);
        Env base = super.createBaseEnv(c);
        ExtractTimeAttribute timeAttribute = new ExtractTimeAttribute(c);
        for(IASTVar p : timeAttribute.getTimeAttributes()){
            if(base.existVarName(p.getName())){
                IASTVar v = base.getVar(p.getName());
                v.setTimeCritical(true);
            }
        }
        //first constructor
        for(IASTMethod m : c.getMethods()){
            if(m instanceof ASTMethod) continue;
            Env eMethod = new Env(base);
            eMethod = CheckExpression.checkPars(m.getParameters(), eMethod);
            analyzeMethod(m, eMethod);
            //analyze(m.getStms(), eMethod );
        }

        //then methods
        for(IASTMethod m : c.getMethods()){
            if(m instanceof ASTConstructor) continue;
            Env eMethod = new Env(base);
            eMethod = CheckExpression.checkPars(m.getParameters(), eMethod);
            analyzeMethod(m, eMethod);
            //analyze(m.getStms(), eMethod );
        }
        if(store){
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("config/" + this.storeName + "_types.csv", true));
                for(TimeTypes t : output){
                    writer.write(t.toString());
                    writer.write("\n");
                    writer.flush();
                }
                writer.close();
            } catch (IOException e) {
                System.err.println("Cannot write the types.csv file");
                System.err.println(e.getMessage());
            }
        }
        return output;
    }

    @Override
    protected void analyzeASTRE(ASTRE r, Env env) {
        CheckExpression.checkRE(r, env);
    }

    @Override
    protected void analyzeASTReturn(ASTReturn elm, Env env) {

        ASTRE re = elm.getExpr();
        if(re != null && re.getExpression() != null && //sanity checks
                CheckExpression.checkIt(re.getExpression(), env)){
            //there is time!
            TimeTypes t = new TimeTypes(this._class.fullName(), lastMethod.getName(), lastMethod.getSignature());
            output.add(t);
        }

    }

}
