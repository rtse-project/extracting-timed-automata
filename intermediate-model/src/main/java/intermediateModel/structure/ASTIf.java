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
public class ASTIf extends IASTStm implements IASTVisitor {

	public class ASTIfStms extends IASTStm implements IASTHasStms {
		List<IASTStm> stms = new ArrayList<>();

		public ASTIfStms(int start, int end) {
			super(start, end);
		}

		@Override
		public void addStms(IASTStm stm) {
			this.stms.add(stm);
		}

		@Override
		public String toString() {
			String out = "";
			for(IASTStm e : stms){
				out += e.toString() + "\n";
			}
			out += "";
			return out;
		}

		@Override
		public List<IASTStm> getStms() {
			return stms;
		}

		@Override
		public void visit(ASTVisitor visitor) {
			for(IASTStm s : stms){
				s.visit(visitor);
			}
		}
	}

	public class ASTElseStms extends IASTStm implements IASTHasStms {
		List<IASTStm> stms = new ArrayList<>();

		public ASTElseStms(int start, int end) {
			super(start, end);
		}

		@Override
		public void addStms(IASTStm stm) {
			this.stms.add(stm);
		}

		@Override
		public String toString() {
			String out = "";
			for(IASTStm e : stms){
				out += e.toString() + "\n";
			}
			out += "";
			return out;
		}

		@Override
		public List<IASTStm> getStms() {
			return stms;
		}

		@Override
		public void visit(ASTVisitor visitor) {
			for(IASTStm s : stms){
				s.visit(visitor);
			}
		}
	}

	protected ASTIfStms ifBranch = null;
	protected ASTElseStms elseBranch = null;
	protected ASTRE guard = null;

	public ASTIf(int start, int end, ASTRE guard) {
		super(start, end);
		this.guard = guard;
	}

	public ASTRE getGuard() {
		return guard;
	}

	@Override
	public String toString() {
		String out = "";
		out += guard.toString() + " )\n";
		if(ifBranch != null)
			out += ifBranch.toString();
		if(elseBranch != null)
			out += elseBranch.toString();
		out += "";
		return out;
	}

	public ASTIfStms getIfBranch() {
		return ifBranch;
	}

	public ASTElseStms getElseBranch() {
		return elseBranch;
	}

	public void setIfBranch(ASTIfStms ifBranch) {
		this.ifBranch = ifBranch;
	}

	public void setElseBranch(ASTElseStms elseBranch) {
		this.elseBranch = elseBranch;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTIf)) return false;

		ASTIf astIf = (ASTIf) o;

		if (getIfBranch() != null ? !getIfBranch().equals(astIf.getIfBranch()) : astIf.getIfBranch() != null)
			return false;
		if (getElseBranch() != null ? !getElseBranch().equals(astIf.getElseBranch()) : astIf.getElseBranch() != null)
			return false;
		if (guard != null ? !guard.equals(astIf.guard) : astIf.guard != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = getIfBranch() != null ? getIfBranch().hashCode() : 0;
		result = 31 * result + (getElseBranch() != null ? getElseBranch().hashCode() : 0);
		result = 31 * result + (guard != null ? guard.hashCode() : 0);
		return result;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTIf(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		guard.visit(visitor);
		ifBranch.visit(visitor);
		if(elseBranch != null)
			elseBranch.visit(visitor);
		visitor.exitASTIf(this);
	}
}
