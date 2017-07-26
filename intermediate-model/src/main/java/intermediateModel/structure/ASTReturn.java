package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTReturn extends IASTStm implements IASTVisitor {

	private ASTRE expr;

	public ASTReturn(int start, int end, ASTRE expr) {
		super(start, end);
		this.expr = expr;
	}

	public ASTRE getExpr() {
		return expr;
	}

	@Override
	public String toString() {
		if(expr != null)
			return expr.toString();
		return "return;";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTReturn)) return false;

		ASTReturn astReturn = (ASTReturn) o;

		if (expr != null ? !expr.equals(astReturn.expr) : astReturn.expr != null) return false;

		return true;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTReturn(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		if(expr != null)
			expr.visit(visitor);
		visitor.exitASTReturn(this);
	}
}
