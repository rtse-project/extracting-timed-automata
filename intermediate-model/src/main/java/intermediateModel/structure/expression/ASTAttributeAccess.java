package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTAttributeAccess extends IASTStm implements IASTRE {

	private String attributeName;
	private IASTRE variableName;


	public ASTAttributeAccess(int start, int end, IASTRE variableName, String attributeName) {
		super(start, end);
		this.variableName = variableName;
		this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public IASTRE getVariableName() {
		return variableName;
	}

	@Override
	public String toString() {
		return "ASTAttributeAccess{" +
				"variableName='" + variableName + "\'," +
				"attributeName='" + attributeName + '\'' +
				'}';
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTAttributeAccess(this);
		if(variableName != null)
			variableName.visit(visitor);
		visitor.exitASTAttributeAccess(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return this;
	}

	@Override
	public String print() {
		return variableName.print() + "." + attributeName;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTAttributeAccess(this);
		if(variableName != null)
			variableName.visit(visitor);
		visitor.exitASTAttributeAccess(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTAttributeAccess that = (ASTAttributeAccess) o;

		if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
			return false;
		return variableName != null ? variableName.equals(that.variableName) : that.variableName == null;
	}

	@Override
	public int hashCode() {
		int result = attributeName != null ? attributeName.hashCode() : 0;
		result = 31 * result + (variableName != null ? variableName.hashCode() : 0);
		return result;
	}
}
