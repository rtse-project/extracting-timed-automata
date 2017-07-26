package intermediateModelHelper.indexing.structure;

import intermediateModel.interfaces.IASTMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * The following class is used to save some data in a MongoDB.
 * The data stored consists in:
 * <ul>
 *     <li>Package name where it belongs to</li>
 *     <li>Class Name where it belongs to</li>
 *     <li>Method where it belongs to</li>
 *     <li>Expression where it synchronized to</li>
 *     <li>Position in the code</li>
 * </ul>
 * The class is just a <i>Struct</i> that serves the purpose of storing information only.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IndexSyncBlock {
	String classPackage = "";
	String name = "";
	String methodName = "";
	List<String> signature = new ArrayList<>();
	String expr = null;
	int start = 0;
	int end = 0;
	int line = 0;
	IndexParameter syncVar;
	boolean isAccessibleFromOutside = false;
	boolean isAccessibleWritingFromOutside = false;
	boolean inheritedVar = false;
	String exprPkg;
	String exprType;
	String path;
	String pkgVar;
	String classVar;

	public IndexSyncBlock() {
	}

	public IndexSyncBlock(IndexSyncBlock s) {
		this.classPackage = s.getPackageName();
		this.name = s.getName();
		this.methodName = s.getMethodName();
		this.expr = s.getExpr();
		this.start = s.getStart();
		this.end = s.getEnd();
		this.line = s.getLine();
		this.syncVar = s.getSyncVar();
		this.signature = s.getSignature();
		this.path = s.getPath();
		this.isAccessibleFromOutside = s.isAccessibleFromOutside();
		this.isAccessibleWritingFromOutside = s.isAccessibleWritingFromOutside();
		this.inheritedVar = s.isInherited();
		this.exprPkg = s.getExprPkg();
		this.exprType = s.getExprType();
		this.pkgVar = s.getPkgVar();
		this.classVar = s.getClassVar();
	}


	public IndexSyncBlock(String packageName, String name, String methodName, String expr, int start, int end, int line, IndexParameter syncVar, boolean isAccessibleFromOutside, boolean isAccessibleWritingFromOutside, boolean inheritedVar, List<String> signature, String exprPkg, String exprType, String path, String pkgVar, String classVar) {
		this.classPackage = packageName;
		this.name = name;
		this.methodName = methodName;
		this.expr = expr;
		this.start = start;
		this.end = end;
		this.line = line;
		this.syncVar = syncVar;
		this.isAccessibleFromOutside = isAccessibleFromOutside;
		this.signature = signature;
		this.exprPkg = exprPkg;
		this.exprType = exprType;
		this.path = path;
		this.isAccessibleWritingFromOutside = isAccessibleWritingFromOutside;
		this.inheritedVar = inheritedVar;
		this.pkgVar = pkgVar;
		this.classVar = classVar;
	}

	public IndexParameter getSyncVar() {
		return syncVar;
	}

	public void setSyncVar(IndexParameter syncVar) {
		this.syncVar = syncVar;
	}

	public String getPackageName() {
		return classPackage;
	}

	public void setPackageName(String packageName) {
		this.classPackage = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String className) {
		this.name = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public boolean isAccessibleFromOutside() {
		return isAccessibleFromOutside;
	}

	public void setAccessibleFromOutside(boolean accessibleFromOutside) {
		isAccessibleFromOutside = accessibleFromOutside;
	}

	public List<String> getSignature() {
		return signature;
	}

	public void setSignature(List<String> signature) {
		this.signature = signature;
	}

	public String getExprPkg() {
		return exprPkg;
	}

	public void setExprPkg(String exprPkg) {
		this.exprPkg = exprPkg;
	}

	public String getExprType() {
		return exprType;
	}

	public void setExprType(String exprType) {
		this.exprType = exprType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IndexSyncBlock)) return false;

		IndexSyncBlock that = (IndexSyncBlock) o;

		if (getStart() != that.getStart()) return false;
		if (getEnd() != that.getEnd()) return false;
		if (getLine() != that.getLine()) return false;
		if (getPackageName() != null ? !getPackageName().equals(that.getPackageName()) : that.getPackageName() != null)
			return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
			return false;
		if (getMethodName() != null ? !getMethodName().equals(that.getMethodName()) : that.getMethodName() != null)
			return false;
		return getExpr() != null ? getExpr().equals(that.getExpr()) : that.getExpr() == null;

	}

	@Override
	public int hashCode() {
		int result = getPackageName() != null ? getPackageName().hashCode() : 0;
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		result = 31 * result + (getMethodName() != null ? getMethodName().hashCode() : 0);
		result = 31 * result + (getExpr() != null ? getExpr().hashCode() : 0);
		result = 31 * result + getStart();
		result = 31 * result + getEnd();
		result = 31 * result + getLine();
		return result;
	}

	public boolean isInMethod(IASTMethod method) {
		if(!this.methodName.equals(method.getName())) return false;
		if(this.signature.size() != method.getParameters().size()) return false;
		boolean flag = true;
		for(int i = 0; i < this.signature.size(); i++){
			if(!signature.get(i).equals(method.getParameters().get(i).getType())){
				flag = false;
			}
		}
		return flag;
	}

	public void setAccessibleWritingFromOutside(boolean accessibleWritingFromOutside) {
		this.isAccessibleWritingFromOutside = accessibleWritingFromOutside;
	}

	public boolean isAccessibleWritingFromOutside() {
		return isAccessibleWritingFromOutside;
	}

	public void setInherited(boolean inherited) {
		this.inheritedVar = inherited;
	}
	public boolean isInherited(){
		return this.inheritedVar;
	}

	public String getPkgVar() {
		return pkgVar;
	}

	public void setPkgVar(String pkgVar) {
		this.pkgVar = pkgVar;
	}

	public String getClassVar() {
		return classVar;
	}

	public void setClassVar(String classVar) {
		this.classVar = classVar;
	}

	public boolean sameInheritance(IndexSyncBlock outter) {
		String pkg = outter.getPkgVar();
		String cls = outter.getClassVar();
		return this.pkgVar.equals(pkg) && this.classVar.equals(cls);
	}
}
