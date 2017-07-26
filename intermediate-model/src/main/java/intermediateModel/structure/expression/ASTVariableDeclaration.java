package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTVariableDeclaration extends IASTStm implements IASTRE {

	String type;
	IASTRE name;
	IASTRE expr;
	private String typePointed;

	public ASTVariableDeclaration(int start, int end, String type, IASTRE name, IASTRE expr) {
		super(start, end);
		this.type = type;
		this.name = name;
		this.expr = expr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public IASTRE getName() {
		return name;
	}

	public void setName(IASTRE name) {
		this.name = name;
	}

	public IASTRE getExpr() {
		return expr;
	}

	public void setExpr(IASTRE expr) {
		this.expr = expr;
	}

	@Override
	public String toString() {
		return "ASTVariableDeclaration{" +
				"type='" + type + '\'' +
				", name='" + name + '\'' +
				", expr=" + expr +
				"}\n";
	}


	public String getNameString() {
		if(name instanceof ASTLiteral)
			return ((ASTLiteral) name).getValue();
		return "--not yet define--";
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visit((ASTREVisitor)visitor);
	}

	@Override
	public String print() {
		StringBuffer bf = new StringBuffer();
		bf.append(type + " " + name.print());
		if(expr != null) {
			bf.append(" = " + expr.print());
		}
		return bf.toString();
	}

	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTVariableDeclaration(this);
		if(name != null)
			name.visit(visitor);
		if(expr != null)
			expr.visit(visitor);
		visitor.exitASTVariableDeclaration(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return this;
	}

	public void setTypePointed(String typePointed) {
		this.typePointed = typePointed;
	}

	public String getTypePointed() {
		return typePointed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTVariableDeclaration that = (ASTVariableDeclaration) o;

		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (expr != null ? !expr.equals(that.expr) : that.expr != null) return false;
		return typePointed != null ? typePointed.equals(that.typePointed) : that.typePointed == null;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (expr != null ? expr.hashCode() : 0);
		result = 31 * result + (typePointed != null ? typePointed.hashCode() : 0);
		return result;
	}
}
