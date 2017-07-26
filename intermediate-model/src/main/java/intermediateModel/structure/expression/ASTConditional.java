package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTConditional extends IASTStm implements IASTRE {

	IASTRE expr;
	IASTRE thenExpr;
	IASTRE elseExpr;

	public ASTConditional(int start, int end, IASTRE expr, IASTRE thenExpr, IASTRE elseExpr) {
		super(start, end);
		this.expr = expr;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}

	public IASTRE getExpr() {
		return expr;
	}

	public IASTRE getThenExpr() {
		return thenExpr;
	}

	public IASTRE getElseExpr() {
		return elseExpr;
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTConditional(this);
		expr.visit(visitor);
		thenExpr.visit(visitor);
		elseExpr.visit(visitor);
		visitor.exitASTConditional(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return new ASTConditional(start, end, expr.negate(), elseExpr, thenExpr);
	}

	@Override
	public String print() {
		return expr.print() + " ? " + thenExpr.print() + " : " + elseExpr.print();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTConditional(this);
		expr.visit(visitor);
		thenExpr.visit(visitor);
		elseExpr.visit(visitor);
		visitor.exitASTConditional(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTConditional that = (ASTConditional) o;

		if (expr != null ? !expr.equals(that.expr) : that.expr != null) return false;
		if (thenExpr != null ? !thenExpr.equals(that.thenExpr) : that.thenExpr != null) return false;
		return elseExpr != null ? elseExpr.equals(that.elseExpr) : that.elseExpr == null;
	}

	@Override
	public int hashCode() {
		int result = expr != null ? expr.hashCode() : 0;
		result = 31 * result + (thenExpr != null ? thenExpr.hashCode() : 0);
		result = 31 * result + (elseExpr != null ? elseExpr.hashCode() : 0);
		return result;
	}
}
