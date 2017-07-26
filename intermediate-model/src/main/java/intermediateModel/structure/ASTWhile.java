package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTHasStms;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTWhile extends IASTStm implements IASTHasStms, IASTVisitor {
	List<IASTStm> stms = new ArrayList<IASTStm>();;
	ASTRE expr;
	List<String> timeVarsInExpr = new ArrayList<>();

	public ASTWhile(int start, int end) {
		super(start, end);
	}

	public ASTWhile(int start, int end, ASTRE expr) {
		super(start, end);
		this.expr = expr;
	}


	public List<IASTStm> getStms() {
		return stms;
	}

	public void setStms(List<IASTStm> stms) {
		this.stms = stms;
	}

	public ASTRE getExpr() {
		return expr;
	}

	public void setExpr(ASTRE expr) {
		this.expr = expr;
	}

	@Override
	public void addStms(IASTStm stm) {
		stms.add(stm);
	}

	@Override
	public String toString() {
		String out = "";
		out += expr.toString();
		out += "\n";
		for(IASTStm e : stms){
			out += e.toString() + "\n";
		}
		out += "";
		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTWhile)) return false;

		ASTWhile astWhile = (ASTWhile) o;

		if (getStms() != null ? !getStms().equals(astWhile.getStms()) : astWhile.getStms() != null) return false;
		if (getExpr() != null ? !getExpr().equals(astWhile.getExpr()) : astWhile.getExpr() != null) return false;

		return true;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTWhile(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		expr.visit(visitor);
		for(IASTStm s : stms){
			s.visit(visitor);
		}
		visitor.exitASTWhile(this);
	}

    public void addTimeVar(String name) {
		this.timeVarsInExpr.add(name);
    }

	public List<String> getTimeVars() {
		return timeVarsInExpr;
	}
}
