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

public class ASTStatic extends IASTStm implements IASTHasStms, IASTVisitor {

	List<IASTStm> stms = new ArrayList<>();

	public ASTStatic(int start, int end) {
		super(start,end);
	}

	@Override
	public String toString() {
		return "ASTStatic{" +
				"stms=" + stms +
				'}';
	}

	public void setStms(List<IASTStm> stms) {
		this.stms = stms;
	}
	public void addStms(IASTStm stm) {
		this.stms.add(stm);
	}
	@Override
	public List<IASTStm> getStms() {
		return stms;
	}


	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTStatic(this);
		for(IASTStm s : stms){
			s.visit(visitor);
		}
		visitor.exitASTStatic(this);
	}

}
