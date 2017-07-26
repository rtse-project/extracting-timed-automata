package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTPreOp extends IASTStm implements IASTRE {

	private IASTRE var;
	private ADDDEC type;

	public ASTPreOp(int start, int end, IASTRE var, ADDDEC type) {
		super(start, end);
		this.var = var;
		this.type = type;
	}

	public ADDDEC getType() {
		return type;
	}

	@Override
	public String toString() {
		return "ASTPreOp{" +
				"var=" + var +
				", type=" + type +
				'}';
	}


	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTPreOp(this);
		var.visit(visitor);
		visitor.exitASTPreOp(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return new ASTPreOp(start,end,var, type==ADDDEC.decrement ? ADDDEC.increment : ADDDEC.decrement);
	}

	@Override
	public String print() {
		return type.print() + var.print();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTPreOp(this);
		var.visit(visitor);
		visitor.exitASTPreOp(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTPreOp astPreOp = (ASTPreOp) o;

		if (var != null ? !var.equals(astPreOp.var) : astPreOp.var != null) return false;
		return type == astPreOp.type;
	}

	@Override
	public int hashCode() {
		int result = var != null ? var.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	public IASTRE getVar() {
		return var;
	}
}
