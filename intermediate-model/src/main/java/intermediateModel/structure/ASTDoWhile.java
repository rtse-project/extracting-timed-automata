package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;


/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTDoWhile extends ASTWhile implements IASTVisitor {

	public ASTDoWhile(int start, int end) {
		super(start, end);
	}

	public ASTDoWhile(int start, int end, ASTRE expr) {
		super(start, end, expr);
		int tmp = this.getLine();
		this.line = this.lineEnd;
		this.lineEnd = tmp;
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
	public void visit(ASTVisitor visitor) {
		visitor.enterASTDoWhile(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		expr.visit(visitor);
		for(IASTStm s : stms){
			s.visit(visitor);
		}
		visitor.exitASTDoWhile(this);
	}
}
