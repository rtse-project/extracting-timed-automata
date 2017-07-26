package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTPostOp extends IASTStm implements IASTRE {

	private IASTRE var;
	private ADDDEC type;

	public ASTPostOp(int start, int end, IASTRE var, ADDDEC type) {
		super(start, end);
		this.var = var;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ASTPostOp{" +
				"var=" + var +
				", type=" + type +
				'}';
	}

	public ADDDEC getType() {
		return type;
	}

	public IASTRE getVar() {
		return var;
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTPostOp(this);
		var.visit(visitor);
		visitor.exitASTPostOp(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return new ASTPostOp(start,end,var, type==ADDDEC.decrement ? ADDDEC.increment : ADDDEC.decrement);
	}

	@Override
	public String print() {
		return var.print() + type.print();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTPostOp(this);
		var.visit(visitor);
		visitor.exitASTPostOp(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTPostOp astPostOp = (ASTPostOp) o;

		if (var != null ? !var.equals(astPostOp.var) : astPostOp.var != null) return false;
		return type == astPostOp.type;
	}

	@Override
	public int hashCode() {
		int result = var != null ? var.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
