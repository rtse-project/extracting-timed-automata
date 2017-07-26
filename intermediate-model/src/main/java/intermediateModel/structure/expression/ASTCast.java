package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTCast extends IASTStm implements IASTRE {

	private String type;
	private IASTRE expr;

	public ASTCast(int start, int end, String type, IASTRE expr) {
		super(start, end);
		this.type = type;
		this.expr = expr;
	}

	public String getType() {
		return type;
	}

	public IASTRE getExpr() {
		return expr;
	}

	@Override
	public String toString() {
		return "ASTCast{" +
				"type='" + type + '\'' +
				", expr=" + expr +
				'}';
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTCast(this);
		expr.visit(visitor);
		visitor.exitASTCast(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return this;
	}

	@Override
	public String print() {
		return "(" + type + ")" + expr.print();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTCast(this);
		expr.visit(visitor);
		visitor.exitASTCast(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTCast astCast = (ASTCast) o;

		if (type != null ? !type.equals(astCast.type) : astCast.type != null) return false;
		return expr != null ? expr.equals(astCast.expr) : astCast.expr == null;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (expr != null ? expr.hashCode() : 0);
		return result;
	}
}
