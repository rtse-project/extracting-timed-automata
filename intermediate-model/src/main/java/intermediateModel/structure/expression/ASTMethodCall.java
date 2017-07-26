package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTMethodCall extends IASTStm implements IASTRE {

	private String methodName;
	private IASTRE exprCallee;
	List<IASTRE> parameters;
	List<String> timePars = new ArrayList<>();
	String classPointed = null;
	boolean isTimeCall = false;
	boolean isMinMax = false;
	private boolean maxMin;

	public ASTMethodCall(int start, int end, String methodName, IASTRE exprCallee) {
		super(start, end);
		this.methodName = methodName;
		this.exprCallee = exprCallee;
	}


	public ASTMethodCall(int start, int end, String methodName, IASTRE exprCallee, List<IASTRE> parameters) {
		super(start, end);
		this.methodName = methodName;
		this.exprCallee = exprCallee;
		this.parameters = parameters;
	}

	public IASTRE getExprCallee() {
		return exprCallee;
	}

	public void setExprCallee(IASTRE exprCallee) {
		this.exprCallee = exprCallee;
	}

	public List<IASTRE> getParameters() {
		return parameters;
	}

	public void setParameters(List<IASTRE> parameters) {
		this.parameters = parameters;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return this.print();
	}

	public String getClassPointed() {
		//remove <>
		if(classPointed != null && classPointed.contains("<")){
			return classPointed.substring(0, classPointed.indexOf("<"));
		}
		return classPointed;
	}

	public void setClassPointed(String classPointed) {
		this.classPointed = classPointed;
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTMethodCall(this);
		if(exprCallee != null)
			exprCallee.visit(visitor);
		if(!visitor.isExcludePars())
			for(IASTRE p : parameters){
				p.visit(visitor);
			}
		visitor.exitASTMethodCall(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return this;
	}

	public boolean isTimeCall() {
		return isTimeCall;
	}

	public void setTimeCall(boolean timeCall) {
		isTimeCall = timeCall;
	}

	@Override
	public String print() {
		if(isTimeCall)
			return "{" + this.printMethodCall() + ":replace}";
		StringBuffer bf = new StringBuffer();
		if(exprCallee != null)
			bf.append(exprCallee.print() + "." + methodName + "(");
		else
			bf.append(methodName + "(");

		for(int i = 0; i < parameters.size(); i++){
			IASTRE p = parameters.get(i);
			if(i != parameters.size()-1){
				bf.append(p.print());
				bf.append(",");
			} else {
				bf.append(p.print());
			}
		}
		bf.append(")");
		return bf.toString();
	}

	public String printMethodCall() {
		StringBuffer bf = new StringBuffer();
		if(exprCallee != null)
			bf.append(exprCallee.print() + "." + methodName + "(");
		else
			bf.append(methodName + "(");
		for(IASTRE p : parameters){
			bf.append(p.print());
			bf.append(",");
		}
		bf.subSequence(0, bf.length()-1);
		bf.append(")");
		return bf.toString();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTMethodCall(this);
		if(exprCallee != null)
			exprCallee.visit(visitor);
		for(IASTRE p : parameters){
			p.visit(visitor);
		}
		visitor.exitASTMethodCall(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTMethodCall that = (ASTMethodCall) o;

		if (isTimeCall != that.isTimeCall) return false;
		if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
		if (exprCallee != null ? !exprCallee.equals(that.exprCallee) : that.exprCallee != null) return false;
		if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
		return classPointed != null ? classPointed.equals(that.classPointed) : that.classPointed == null;
	}

	@Override
	public int hashCode() {
		int result = methodName != null ? methodName.hashCode() : 0;
		result = 31 * result + (exprCallee != null ? exprCallee.hashCode() : 0);
		result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
		result = 31 * result + (classPointed != null ? classPointed.hashCode() : 0);
		result = 31 * result + (isTimeCall ? 1 : 0);
		return result;
	}

	public boolean isMaxMin() {
		return maxMin;
	}

	public void setMaxMin(boolean maxMin) {
		this.maxMin = maxMin;
	}

	public void addTimeVar(String name){
		if(!this.timePars.contains(name))
			this.timePars.add(name);
	}

	public List<String> getTimePars() {
		return timePars;
	}
}