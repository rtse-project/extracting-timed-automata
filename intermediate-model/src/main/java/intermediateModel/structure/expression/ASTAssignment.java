package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTAssignment extends IASTStm implements IASTRE {

	protected IASTRE left;
	protected IASTRE right;
	protected OPERATOR op;

	public ASTAssignment(int start, int end, IASTRE left, IASTRE right, OPERATOR op) {
		super(start, end);
		this.left = left;
		this.right = right;
		this.op = op;
	}

	public IASTRE getLeft() {
		return left;
	}

	public void setLeft(IASTRE left) {
		this.left = left;
	}

	public IASTRE getRight() {
		return right;
	}

	public void setRight(IASTRE right) {
		this.right = right;
	}

	public OPERATOR getOp() {
		return op;
	}

	public void setOp(OPERATOR op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return "ASTAssignment{" +
				"left=" + left +
				", right=" + right +
				", op=" + op +
				'}';
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTAssignment(this);
		left.visit(visitor);
		right.visit(visitor);
		visitor.exitASTAssignment(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return this;
	}

	@Override
	public String print() {
		return left.print() + op.print() + right.print();
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTAssignment(this);
		left.visit(visitor);
		right.visit(visitor);
		visitor.exitASTAssignment(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTAssignment that = (ASTAssignment) o;

		if (left != null ? !left.equals(that.left) : that.left != null) return false;
		if (right != null ? !right.equals(that.right) : that.right != null) return false;
		return op == that.op;
	}

	@Override
	public int hashCode() {
		int result = left != null ? left.hashCode() : 0;
		result = 31 * result + (right != null ? right.hashCode() : 0);
		result = 31 * result + (op != null ? op.hashCode() : 0);
		return result;
	}
}
