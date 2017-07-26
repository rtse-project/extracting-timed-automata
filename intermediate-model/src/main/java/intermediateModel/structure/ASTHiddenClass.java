package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTVisitor;

import java.util.ArrayList;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

public class ASTHiddenClass extends ASTClass implements IASTVisitor {

	private static int _numberHidden = 0;

	public ASTHiddenClass(int start, int end) {
		super(start, end, "", "anonymous" + _numberHidden, Visibility.HIDDEN, "", new ArrayList<String>());
	}

	public int getIdHidden() {
		return _numberHidden;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTHiddenClass(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		for(IASTMethod m : methods){
			m.visit(visitor);
		}
		for(ASTImport i : imports){
			i.visit(visitor);
		}
		for(ASTAttribute a : attributes){
			a.visit(visitor);
		}
		visitor.exitASTHiddenClass(this);
	}
}
