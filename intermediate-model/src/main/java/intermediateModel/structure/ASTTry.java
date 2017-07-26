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
public class ASTTry extends IASTStm implements IASTVisitor {

	ASTTryBranch tryBranch = null;
	List<ASTCatchBranch> catchBranch = new ArrayList<>();
	ASTFinallyBranch finallyBranch = null;

	public class ASTTryBranch extends IASTStm implements IASTHasStms {
		List<IASTStm> stms = new ArrayList<>();

		public ASTTryBranch(int start, int end) {
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
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ASTTryBranch)) return false;

			ASTTryBranch tryBranch = (ASTTryBranch) o;

			if (getStms() != null ? !getStms().equals(tryBranch.getStms()) : tryBranch.getStms() != null) return false;

			return true;
		}

		@Override
		public void visit(ASTVisitor visitor) {
			for(IASTStm s : stms){
				s.visit(visitor);
			}
		}
	}

	public class ASTCatchBranch extends IASTStm implements IASTHasStms {
		List<IASTStm> stms = new ArrayList<>();
		ASTVariable expr = null;

		public ASTCatchBranch(int start, int end, ASTVariable expr) {
			super(start, end);
			this.expr = expr;
		}

		public ASTVariable getExpr() {
			return expr;
		}

		@Override
		public void addStms(IASTStm stm) {
			this.stms.add(stm);
		}

		@Override
		public String toString() {
			String out = "";
			if(expr != null)
				out += expr.toString();
			out += "\n";
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
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ASTCatchBranch)) return false;

			ASTCatchBranch that = (ASTCatchBranch) o;

			if (getStms() != null ? !getStms().equals(that.getStms()) : that.getStms() != null) return false;
			if (expr != null ? !expr.equals(that.expr) : that.expr != null) return false;

			return true;
		}

		@Override
		public void visit(ASTVisitor visitor) {
			visitor.enterSTM(this);
			visitor.exitSTM(this);
			expr.visit(visitor);
			for(IASTStm s : stms){
				s.visit(visitor);
			}
		}
	}

	public class ASTFinallyBranch extends IASTStm implements IASTHasStms {
		List<IASTStm> stms = new ArrayList<>();
		public ASTFinallyBranch(int start, int end) {
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
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ASTFinallyBranch)) return false;

			ASTFinallyBranch that = (ASTFinallyBranch) o;

			if (getStms() != null ? !getStms().equals(that.getStms()) : that.getStms() != null) return false;

			return true;
		}

		@Override
		public void visit(ASTVisitor visitor) {
			visitor.enterSTM(this);
			visitor.exitSTM(this);
			for(IASTStm s : stms){
				s.visit(visitor);
			}
		}
	}


	public ASTTry(int start, int end) {
		super(start, end);
	}

	@Override
	public String toString() {
		String out = "";
		if(tryBranch != null)
			out += tryBranch.toString();
		if(catchBranch.size() > 0)
			for(ASTCatchBranch c : catchBranch)
				out += c.toString();
		if(finallyBranch != null)
			out += finallyBranch.toString();
		out += "";
		return out;
	}

	public ASTTryBranch getTryBranch() {
		return tryBranch;
	}

	public void setTryBranch(ASTTryBranch tryBranch) {
		this.tryBranch = tryBranch;
	}

	public List<ASTCatchBranch> getCatchBranch() {
		return catchBranch;
	}

	public void setCatchBranch(List<ASTCatchBranch> catchBranch) {
		this.catchBranch = catchBranch;
	}
	public void addCatchBranch(ASTCatchBranch catchBranch) {
		this.catchBranch.add(catchBranch);
	}

	public ASTFinallyBranch getFinallyBranch() {
		return finallyBranch;
	}

	public void setFinallyBranch(ASTFinallyBranch finallyBranch) {
		this.finallyBranch = finallyBranch;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTTry)) return false;

		ASTTry astTry = (ASTTry) o;

		if (getTryBranch() != null ? !getTryBranch().equals(astTry.getTryBranch()) : astTry.getTryBranch() != null)
			return false;
		if (getCatchBranch() != null ? !getCatchBranch().equals(astTry.getCatchBranch()) : astTry.getCatchBranch() != null)
			return false;
		if (getFinallyBranch() != null ? !getFinallyBranch().equals(astTry.getFinallyBranch()) : astTry.getFinallyBranch() != null)
			return false;

		return true;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTTry(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		tryBranch.visit(visitor);
		for(ASTCatchBranch c : getCatchBranch()){
			c.visit(visitor);
		}
		if(finallyBranch != null)
			finallyBranch.visit(visitor);
		visitor.exitASTTry(this);
	}

}
