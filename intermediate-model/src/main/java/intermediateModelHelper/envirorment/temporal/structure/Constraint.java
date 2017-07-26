package intermediateModelHelper.envirorment.temporal.structure;

import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.expression.ASTLiteral;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.heuristic.definition.SearchTimeConstraint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by giovanni on 22/03/2017.
 */
public class Constraint {
    IASTStm elm;
    String category;
    String value;
    int line;
    List<RuntimeConstraint> runtimeConstraints = new ArrayList<>();

    public Constraint(IASTStm elm, String category, String value, int line) {
        this.elm = elm;
        this.category = category;
        this.value = value;
        this.line = line;
    }

    public Constraint(IASTStm elm, Class category, String value, int line, ASTClass c, String methodName){
        this(elm,category,value,line,c,methodName,true);
    }
    public Constraint(IASTStm elm, Class category, String value, int line, ASTClass c, String methodName, boolean calculateRuntime) {
        this(elm, category.getCanonicalName(), value, line);
        String className = c.getPackageName() + "." + c.getName();
        if(calculateRuntime) {
            elm.visit(new DefaultASTVisitor() {
                List<ASTLiteral> visited = new ArrayList<>();

                @Override
                public void enterASTMethodCall(ASTMethodCall elm) {
                    if (!elm.isTimeCall())
                        return;
                    RuntimeConstraint rntCnst = new RuntimeConstraint(className, methodName, line, elm.printMethodCall());
                    runtimeConstraints.add(rntCnst);
                    elm.visit(new DefualtASTREVisitor() {
                        @Override
                        public void enterASTLiteral(ASTLiteral elm) {
                            visited.add(elm);
                        }
                    });
                }

                @Override
                public void enterASTLiteral(ASTLiteral elm) {
                    if (visited.contains(elm))
                        return;
                    //we should skip strings and integers
                    String val = elm.getValue();
                    if (val.startsWith("\"") || ( val.length() > 1 && val.substring(0, 1).matches("[0-9]")) ) {
                        return;
                    }
                    RuntimeConstraint rntCnst = new RuntimeConstraint(className, methodName, line, val);
                    runtimeConstraints.add(rntCnst);
                }
            });
        }
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        if(value.startsWith(".")){
            String tmp = value.substring(1); //no first dot
            int first = tmp.indexOf("(");
            int last = tmp.lastIndexOf(")");
            String firstPart = tmp.substring(0, first);
            String lastPart = tmp.substring(last + 1);
            return firstPart+lastPart;
        }
        return value;
    }

    public int getLine() {
        return line;
    }

    public IASTStm getElm() {
        return elm;
    }

    public List<RuntimeConstraint> getRuntimeConstraints() {
        return runtimeConstraints;
    }

    public void setRuntimeConstraints(List<RuntimeConstraint> runtimeConstraints) {
        this.runtimeConstraints = runtimeConstraints;
    }
    public void addRuntimeConstraints(RuntimeConstraint runtimeConstraints) {
        this.runtimeConstraints.add(runtimeConstraints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Constraint that = (Constraint) o;

        if (line != that.line) return false;
        if (elm != null ? !elm.equals(that.elm) : that.elm != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int result = elm != null ? elm.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + line;
        return result;
    }

    public boolean isCategory(Class<? extends SearchTimeConstraint> _class){
        return this.category.equals(_class.getCanonicalName());
    }

    public boolean isCategory(Class<? extends SearchTimeConstraint>[] _class){
        boolean f = false;
        for(Class c : _class){
            f = f || this.category.equals(c.getCanonicalName());
        }
        return f;
    }

    public Constraint negate() {
        String neg_value = this.value;
        if(neg_value.contains("<="))
            neg_value = neg_value.replace("<=", ">");
        else if(neg_value.contains(">="))
            neg_value = neg_value.replace(">=", "<");
        else if(neg_value.contains("=="))
            neg_value = neg_value.replace("==", "<") + " || " + neg_value.replace("==", ">");
        else if(neg_value.contains("!="))
            neg_value = neg_value.replace("!=", "==");
        else if(neg_value.contains("<"))
            neg_value = neg_value.replace("<", ">=");
        else if(neg_value.contains(">"))
            neg_value = neg_value.replace(">", "<=");
        Constraint c = new Constraint(this.elm, this.category, neg_value, this.line);
        c.setRuntimeConstraints(this.runtimeConstraints);
        return c;
    }

    public String runtimeConstraintList(){
        return runtimeConstraintList(false);
    }

    public String runtimeConstraintList(boolean header){
        runtimeConstraints.sort(Comparator.comparing(RuntimeConstraint::getClassName).thenComparing(RuntimeConstraint::getLine));
        StringBuilder out = new StringBuilder();
        if(header)
            out.append("class;method;line;var\n");
        for(RuntimeConstraint r : runtimeConstraints){
            out.append(r.toString());
            out.append("\n");
        }
        return out.toString();
    }

    public void removeElm() {
        this.elm = null;
    }

}

