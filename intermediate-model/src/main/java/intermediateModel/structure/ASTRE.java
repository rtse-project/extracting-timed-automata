package intermediateModel.structure;

import intermediateModel.interfaces.*;
import intermediateModel.structure.expression.ASTBinary;
import intermediateModel.structure.expression.ASTLiteral;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.structure.expression.ASTVariableDeclaration;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.Env;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTRE extends IASTStm implements IASTVisitor {

	IASTRE expression;
	private static int _ID = 0;
	List<String> usedVars = new ArrayList<>();
	Env env = new Env();
	private boolean isResetTime = false;
	private String resetExpression = "";
	private String type;

	public ASTRE(int start, int end, IASTRE expression) {
		super(start, end);
		this.expression = expression;
		setUsedVars();
	}



	private void setUsedVars() {
		if(this.expression == null) return;
		expression.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTLiteral(ASTLiteral elm) {
				usedVars.add(elm.getValue());
			}
		});
	}

	public List<DeclaredVar> getEnv() {
		Env tmp = this.env;
		List<DeclaredVar> out = new ArrayList<>();
		List<String> usedVars = new ArrayList<>();
		while(tmp != null){
			List<IASTVar> vars = tmp.getVarList();
			for(IASTVar v : vars){
				if(!usedVars.contains(v.getName())){
					usedVars.add(v.getName());
					String t = (v.getTypePointed() != null && !v.getTypePointed().equals("")) ? v.getTypePointed() : v.getType();
					out.add(new DeclaredVar(t,v.getName(), v.getName()));
				}
			}
			tmp = tmp.getPrev();
		}
		return out;
	}

	public void setEnv(Env env) {
		this.env = env;
	}

	public String getExpressionName(){
		if(expression instanceof ASTVariableDeclaration){
			final String[] var_name = new String[1];
			((ASTVariableDeclaration) expression).getName().visit(new DefaultASTVisitor(){
				@Override
				public void enterASTLiteral(ASTLiteral elm) {
					var_name[0] = elm.getValue();
				}
			});
			return "Declaration_" + var_name[0];
		}
		if(expression instanceof ASTBinary){
			IASTRE.OPERATOR op = ((ASTBinary) expression).getOp();
			return op.toString();// + "_" + _ID++;
		}
		if(expression instanceof ASTMethodCall){
			return "call_to_" + ((ASTMethodCall) expression).getMethodName();
		}
		if(expression instanceof ASTLiteral){
			return ((ASTLiteral) expression).getValue();
		}
		return expression.getClass().getSimpleName();// + "_" + _ID++;
	}

	private String escape(String name){
		if(name == null) {
			return "";
		}
		name = name.replace("\"","");
		name = name.replace(" ","");
		return name;
	}

	@Override
	public String toString() {
		if(expression == null)
			return "::RE NULL::";
		//return "::::REXP:::" + expression.toString();
		return expression.print();
	}

	public String toText() {
		if(expression == null)
			return expression.getCode();
		return "";
	}

	public List<String> getUsedVars() {
		return usedVars;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTRE)) return false;

		ASTRE astre = (ASTRE) o;

		if(astre.getLine() != this.getLine()) return false;
		if(astre.getStart() != this.getStart()) return false;
		if(astre.getEnd() != this.getEnd()) return false;

		return true;

	}

	@Override
	public int hashCode() {
		return getExpression() != null ? getExpression().hashCode() : 0;
	}

	public IASTRE getExpression() {
		return expression;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTRE(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		if(expression != null)
			expression.visit(visitor);
		visitor.exitASTRE(this);
	}

    public void markResetTime() {
		this.isResetTime = true;
    }

    public boolean getIsResetTime(){
		return this.isResetTime;
	}

    public String print() {
    	StringBuffer bf = new StringBuffer();
    	if(this.expression != null){
			bf.append(expression.print());
		}
		return bf.toString();
    }

	public String getResetExpression() {
		return resetExpression;
	}

	public void setResetExpression(String resetExpression) {
		this.resetExpression = resetExpression;
	}

    public void setType(String type) {
        this.type = type;
    }

	public String getType() {
		return type;
	}
}
