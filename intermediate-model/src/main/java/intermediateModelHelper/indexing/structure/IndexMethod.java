package intermediateModelHelper.indexing.structure;

import intermediateModel.structure.ASTVariable;
import intermediateModelHelper.types.DataTreeType;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * The following class is used to save some data in a MongoDB.
 * The data stored consists in:
 * <ul>
 *     <li>Name of the method</li>
 *     <li>List of {@link IndexParameter} parameters</li>
 *     <li>List of exceptions thrown</li>
 *     <li>Position in the source code</li>
 *     <li>it it is a constructor or a sync method</li>
 * </ul>
 * The class is just a <i>Struct</i> that serves the purpose of storing information only.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IndexMethod {
	String packageName = "";
	String fullpackageName = "";
	String name = "";
	String returnType = "";
	String fromClass = "";
	List<IndexParameter> parameters = new ArrayList<>();
	List<String> exceptionsThrowed = new ArrayList<>();
	int start = 0;
	int end = 0;
	int line = 0;
	boolean isConstructor = false;
	boolean isSync = false;
	boolean isAbs = false;
	boolean isStatic = false;

	public IndexMethod(String packageName, String fullpackageName, String name, String returnType, String fromClass, List<IndexParameter> parameters, List<String> exceptionsThrowed, int start, int end, int line, boolean isConstructor, boolean isSync, boolean isAbs, boolean isStatic) {
		this.packageName = packageName;
		this.fullpackageName = fullpackageName;
		this.name = name;
		this.returnType = returnType;
		this.fromClass = fromClass;
		this.parameters = parameters;
		this.exceptionsThrowed = exceptionsThrowed;
		this.start = start;
		this.end = end;
		this.line = line;
		this.isConstructor = isConstructor;
		this.isSync = isSync;
		this.isAbs = isAbs;
		this.isStatic = isStatic;
	}



	public static List<IndexParameter> convertPars(List<ASTVariable> parameters) {
		List<IndexParameter> l = new ArrayList<>();
		for(ASTVariable v : parameters){
			l.add(new IndexParameter(v.getType(), v.getName()));
		}
		return l;
	}

	public boolean equalBySignature(IndexMethod m){
		boolean flag = true;
		if(m.getName().equals(this.getName()) && m.getPackageName().equals(this.getPackageName()) && m.getParameters().size() == this.getParameters().size()){
			//they are equal for class and package and number of parameter, check the signature
			for(int i = 0; i < m.getParameters().size(); i++){
				String t1 = m.getParameters().get(i).getType();
				String t2 = this.getParameters().get(i).getType();
				if(!t1.equals(t2)){
					flag = false;
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	public boolean equalBySignature(String name, List<Pair<String,String>> parsType){
		boolean flag = true;
		if(name.equals(this.getName()) && parsType.size() == this.getParameters().size()){
			//they are equal for class and package and number of parameter, check the signature
			for(int i = 0; i < parsType.size(); i++){
				String t1 = this.getParameters().get(i).getType();
				String t2 = parsType.get(i).getValue0();
				if(!DataTreeType.checkEqualsTypes(t1,t2, this.getPackageName(), parsType.get(i).getValue1() )){
					flag = false;
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	public IndexMethod() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IndexParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<IndexParameter> parameters) {
		this.parameters = parameters;
	}

	public List<String> getExceptionsThrowed() {
		return exceptionsThrowed;
	}

	public void setExceptionsThrowed(List<String> exceptionsThrowed) {
		this.exceptionsThrowed = exceptionsThrowed;
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

	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean constructor) {
		isConstructor = constructor;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean sync) {
		isSync = sync;
	}

	public boolean isAbs() {
		return isAbs;
	}

	public void setAbs(boolean abs) {
		isAbs = abs;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFullpackageName() {
		return fullpackageName;
	}

	public void setFullpackageName(String fullpackageName) {
		this.fullpackageName = fullpackageName;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getFromClass() {
		return fromClass;
	}

	public void setFromClass(String fromClass) {
		this.fromClass = fromClass;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}
}
