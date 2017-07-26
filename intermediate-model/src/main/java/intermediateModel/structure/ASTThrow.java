package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTThrow extends IASTStm implements IASTVisitor {

	private ASTRE expr;

	public ASTThrow(int start, int end, ASTRE expr) {
		super(start, end);
		this.expr = expr;
	}

	public ASTRE getExpr() {
		return expr;
	}

	@Override
	public String toString() {
		return expr.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTThrow)) return false;

		ASTThrow astReturn = (ASTThrow) o;

		if (expr != null ? !expr.equals(astReturn.expr) : astReturn.expr != null) return false;

		return true;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTThrow(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		expr.visit(visitor);
		visitor.exitASTThrow(this);
	}
}
