package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTBreak extends IASTStm implements IASTVisitor {

	private String target = "";

	public ASTBreak(int start, int end){ super(start,end);}

	public ASTBreak(int start, int end, String target) {
		super(start, end);
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTBreak(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		visitor.exitASTBreak(this);
	}
}
