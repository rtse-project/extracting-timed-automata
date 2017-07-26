package intermediateModel.structure.expression;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.ASTHiddenClass;
import intermediateModel.structure.ASTRE;
import intermediateModel.visitors.DefaultASTVisitor;

import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTNewObject extends IASTStm implements IASTRE {

	List<IASTRE> parameters;
	String typeName;
	boolean isArray = false;
	ASTHiddenClass hiddenClass = null;

	@Override
	public String print() {
		StringBuffer bf = new StringBuffer();
		bf.append("new " + typeName);
		if(isArray)
			bf.append("[]");
		bf.append("(");
		for(IASTRE p : parameters){
			bf.append(p.print());
			bf.append(",");
		}
		bf.subSequence(0,bf.length()-1);
		bf.append(")");
		return bf.toString();
	}

	public ASTNewObject(int start, int end, String typeName, boolean isArray) {
		super(start, end);
		this.typeName = typeName;
		this.isArray = isArray;
	}

	public ASTNewObject(int start, int end, String typeName, boolean isArray, List<IASTRE> parameters) {
		super(start, end);
		this.typeName = typeName;
		this.isArray = isArray;
		this.parameters = parameters;
	}

	public void setHiddenClass(ASTHiddenClass hiddenClass) {
		this.hiddenClass = hiddenClass;
	}

	public ASTHiddenClass getHiddenClass() {
		return hiddenClass;
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean array) {
		isArray = array;
	}

	public List<IASTRE> getParameters() {
		return parameters;
	}

	public void setParameters(List<IASTRE> parameters) {
		this.parameters = parameters;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		return "ASTNewObject{" +
				"parameters=" + parameters +
				", typeName='" + typeName + '\'' +
				'}';
	}


	@Override
	public void visit(ASTREVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTNewObject(this);
		if(!visitor.isExcludePars())
			for(IASTRE p : parameters){
				p.visit(visitor);
			}
		if(this.hiddenClass != null && !visitor.isExcludeHiddenClasses()){
			this.hiddenClass.visit(new DefaultASTVisitor(){
				@Override
				public void enterASTRE(ASTRE elm) {
					if(elm.getExpression() != null)
						elm.getExpression().visit(visitor);
				}
			});
		}
		visitor.exitASTNewObject(this);
		visitor.exitAll(this);
	}

	@Override
	public IASTRE negate() {
		return this;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterAll(this);
		visitor.enterASTNewObject(this);
		for(IASTRE p : parameters){
			p.visit(visitor);
		}
		if(this.hiddenClass != null && !visitor.isExcludeHiddenClasses()){
			this.hiddenClass.visit(visitor);
		}
		visitor.exitASTNewObject(this);
		visitor.exitAll(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ASTNewObject that = (ASTNewObject) o;

		if (isArray != that.isArray) return false;
		if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
		if (typeName != null ? !typeName.equals(that.typeName) : that.typeName != null) return false;
		return hiddenClass != null ? hiddenClass.equals(that.hiddenClass) : that.hiddenClass == null;
	}

	@Override
	public int hashCode() {
		int result = parameters != null ? parameters.hashCode() : 0;
		result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
		result = 31 * result + (isArray ? 1 : 0);
		result = 31 * result + (hiddenClass != null ? hiddenClass.hashCode() : 0);
		return result;
	}
}

