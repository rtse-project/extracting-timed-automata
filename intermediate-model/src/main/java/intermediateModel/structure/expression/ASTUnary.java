package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTUnary extends IASTStm implements IASTRE {

	private OPERATOR op;
	private IASTRE expr;

	public ASTUnary(int start, int end, OPERATOR op, IASTRE expr) {
		super(start, end);
		this.op = op;
		this.expr = expr;
	}

	public OPERATOR getOp() {
		return op;
	}

	public IASTRE getExpr() {
		return expr;
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTUnary(this);
		expr.visit(visitor);
		visitor.exitASTUnary(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		if(op == OPERATOR.not) {
			return expr.negate();
		}
		return this;
	}

	@Override
	public String print() {
		return op.print() + expr.print();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTUnary(this);
		expr.visit(visitor);
		visitor.exitASTUnary(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTUnary astUnary = (ASTUnary) o;

		if (op != astUnary.op) return false;
		return expr != null ? expr.equals(astUnary.expr) : astUnary.expr == null;
	}

	@Override
	public int hashCode() {
		int result = op != null ? op.hashCode() : 0;
		result = 31 * result + (expr != null ? expr.hashCode() : 0);
		return result;
	}
}
