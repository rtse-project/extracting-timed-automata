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
public class ASTSwitch extends IASTStm {

	public class ASTCase extends IASTStm implements IASTHasStms, IASTVisitor {
		List<String> labels = new ArrayList<>();
		List<IASTStm> stms = new ArrayList<>();

		public ASTCase(int start, int end, String label) {
			super(start, end);
			this.labels.add(label);
		}

		public ASTCase(int start, int end, List<String> labels) {
			super(start, end);
			this.labels = labels;
		}

		@Override
		public void addStms(IASTStm stm) {
			this.stms.add(stm);
		}

		public void addStms(String label) {
			this.labels.add(label);
		}

		@Override
		public List<IASTStm> getStms() {
			return stms;
		}

		public List<String> getLabels() {
			return labels;
		}

		@Override
		public String toString() {
			String out = "";
			for(String label : labels)
				out += "case " + label + ": \n";
			for(IASTStm e : stms){
				out += e.toString() + "\n";
			}
			out += "";
			return out;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ASTCase)) return false;

			ASTCase astCase = (ASTCase) o;

			if (labels != null ? !labels.equals(astCase.labels) : astCase.labels != null) return false;
			if (getStms() != null ? !getStms().equals(astCase.getStms()) : astCase.getStms() != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = labels != null ? labels.hashCode() : 0;
			result = 31 * result + (getStms() != null ? getStms().hashCode() : 0);
			return result;
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


	ASTRE expr;
	List<ASTCase> cases = new ArrayList<>();

		public ASTSwitch(int start, int end, ASTRE expr) {
		super(start, end);
		this.expr = expr;
	}

	public void addCase(ASTCase stm){
		this.cases.add(stm);
	}

	public ASTRE getExpr() {
		return expr;
	}

	public void setExpr(ASTRE expr) {
		this.expr = expr;
	}

	public List<ASTCase> getCases() {
		return cases;
	}

	@Override
	public String toString() {
		String out = "switch(";
		out += expr.toString();
		out += ")\n";
		for(ASTCase e : cases){
			out += e.toString() + "\n";
		}
		out += "\nendSwitch\n";
		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTSwitch)) return false;

		ASTSwitch astSwitch = (ASTSwitch) o;

		if (getExpr() != null ? !getExpr().equals(astSwitch.getExpr()) : astSwitch.getExpr() != null) return false;
		if (this.cases != null ? !this.cases.equals(astSwitch.cases) : astSwitch.cases != null) return false;

		return true;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTSwitch(this);
		visitor.enterSTM(this);
		visitor.exitSTM(this);
		expr.visit(visitor);
		for(ASTCase c : cases){
			c.visit(visitor);
		}
		visitor.exitASTSwitch(this);
	}
}
