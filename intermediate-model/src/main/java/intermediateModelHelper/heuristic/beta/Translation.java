package intermediateModelHelper.heuristic.beta;

import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTBinary;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.Env;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giovanni on 29/03/2017.
 */
public class Translation {

    public static void Translate(ASTRE re){
        IASTRE expr = re.getExpression();
        if(expr == null) return;
        expr.visit(new DefualtASTREVisitor(){
            @Override
            public void enterASTbinary(ASTBinary elm) {
                if(elm.isTimeCritical()){
                    propagate(elm);
                    return;
                }
                if(!hasTimeCritical(elm.getLeft())){
                    elm.setLeft(null);
                    elm.setOp(IASTRE.OPERATOR.empty);
                }
                if(!hasTimeCritical(elm.getRight())){
                    elm.setRight(null);
                    elm.setOp(IASTRE.OPERATOR.empty);
                }
            }

            private void propagate(ASTBinary elm) {
                elm.visit(new DefualtASTREVisitor(){
                    @Override
                    public void enterAll(IASTRE elm) {
                        elm.setTimeCritical(true);
                    }
                });
            }
        });
    }

    private static boolean hasTimeCritical(IASTRE left) {
        if(left.isTimeCritical()) return true;
        boolean[] flag = {false};
        left.visit(new DefualtASTREVisitor(){
            @Override
            public void enterASTbinary(ASTBinary elm) {
                if(elm.isTimeCritical()){
                    flag[0] = true;
                }
            }
        });
        return flag[0];
    }

    public static void Translate(ASTRE re, Env e){
        Translate(re);
        IASTRE expr = re.getExpression();
        if(expr == null) return;
        expr.visit(new DefualtASTREVisitor(){
            @Override
            public void enterASTMethodCall(ASTMethodCall elm) {
                if(elm.getClassPointed() != null && !elm.getClassPointed().equals("")){
                    if(e.existMethodTimeRelevant(elm.getClassPointed(), elm.getMethodName(), getSignature(elm.getParameters(), e))){
                        //elm.setMethodName("t");
                        //elm.setExprCallee(new ASTLiteral(0,0,""));
                        elm.setTimeCall(true);
                    }
                }
                else if(e.existMethodTimeRelevant(elm.getMethodName(), getSignature(elm.getParameters(), e))){
                    //elm.setMethodName("t");
                    //elm.setExprCallee(new ASTLiteral(0,0,""));
                    elm.setTimeCall(true);
                }
            }
        });
    }

    private static List<String> getSignature(List<IASTRE> parameters, Env where){
        List<String> out = new ArrayList<>();
        for(IASTRE p : parameters){
            out.add(where.getExprType(p));
        }
        return out;
    }
}
