package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTImport extends IASTStm implements IASTVisitor {
	private boolean isStatic = false;
	String packagename = "";

	public ASTImport(int start, int end, boolean isStatic, String packagename) {
		super(start,end);
		this.isStatic = isStatic;
		this.packagename = packagename;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	@Override
	public String toString() {
		return "import " + (isStatic ? "static " : "") + packagename + ";\n";
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTImport(this);
		visitor.exitASTImport(this);
	}
}
