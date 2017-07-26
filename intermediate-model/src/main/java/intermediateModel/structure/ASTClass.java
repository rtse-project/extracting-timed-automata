package intermediateModel.structure;

import intermediateModel.interfaces.ASTVisitor;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

public class ASTClass extends IASTStm implements IASTVisitor {

	String packageName;
	List<IASTMethod> methods = new ArrayList<>();
	List<ASTStatic> staticInit = new ArrayList<>();
	String name;
	Visibility accessRight;
	List<String> implementsInterfaces;
	String extendClass;
	List<ASTImport> imports = new ArrayList<>();
	List<ASTAttribute> attributes = new ArrayList<>();
	String path;
	ASTClass parent = null;
	boolean isInterface = false;
	boolean isAbstract = false;

	public ASTClass(){}

	public ASTClass(int start, int end, String packageName, String name, Visibility accessRight, String extendClass, List<String> implmentsInterfaces){
		super(start,end);
		this.packageName = packageName;
		this.name = name;
		this.accessRight = accessRight;
		this.extendClass = extendClass == null ? "Object" : extendClass;
		this.implementsInterfaces = implmentsInterfaces;
	}

	public ASTClass(int start, int end, String packageName, String name, Visibility accessRight, String extendClass, List<String> implmentsInterfaces, List<IASTMethod> methods) {
		super(start,end);
		this.packageName = packageName;
		this.methods = methods;
		this.name = name;
		this.accessRight = accessRight;
		this.extendClass = extendClass == null ? "Object" : extendClass;
		this.implementsInterfaces = implmentsInterfaces;
	}

	public ASTClass getParent() {
		return parent;
	}

	public void setParent(ASTClass c){
		this.parent = c;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ASTImport> getImports() {
		return imports;
	}

	public void setImports(List<ASTImport> imports) {
		this.imports = imports;
	}

	public List<IASTMethod> getAllMethods() {
		List<IASTMethod> out = new ArrayList<>(methods);
		if(this.parent != null){
			out.addAll(this.parent.getAllMethods());
		}
		return out;
	}

	public List<IASTMethod> getMethods() {
		return this.methods;
	}

	public String getName() {
		return name;
	}

	public Visibility getAccessRight() {
		return accessRight;
	}

	public List<String> getImplmentsInterfaces() {
		return implementsInterfaces;
	}

	public String getExtendClass() {
		if(extendClass.contains("<")){
			return extendClass.substring(0, extendClass.indexOf("<"));
		} else
			return extendClass;
	}

	public void addMethod(IASTMethod method){
		methods.add(method);
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAccessRight(Visibility accessRight) {
		this.accessRight = accessRight;
	}

	public void setImplmentsInterfaces(List<String> implmentsInterfaces) {
		this.implementsInterfaces = implmentsInterfaces;
	}

	public void setExtendClass(String extendClass) {
		this.extendClass = extendClass;
	}

	public void setMethods(List<IASTMethod> methods) {
		this.methods = methods;
	}

	public void addAttribute(ASTAttribute attribute) {
		this.attributes.add(attribute);
	}

	public List<ASTAttribute> getAttributes() {
		return attributes;
	}

	public List<ASTStatic> getStaticInit() {
		return staticInit;
	}

	public void addStaticInit(ASTStatic s){
		this.staticInit.add(s);
	}

	public void setStaticInit(List<ASTStatic> staticInit) {
		this.staticInit = staticInit;
	}

	public void setAttributes(List<ASTAttribute> attributes) {
		this.attributes = attributes;
	}

	public String toString(){
		String out = "";
		out = packageName + "." + name;// + "\n";
		//for(ASTImport imp : imports){
		//	out += imp.toString();
		//}
		//for(ASTAttribute a : attributes){
		//	out += a.toString();
		//}
		//for(IASTMethod m : methods){
		//	out += m.toString();
		//}
		return out;
	}

	public String fullName(){
		String out = packageName + "." + name;
		if(out.startsWith(".")){
			out = out.substring(1);
		}
		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ASTClass)) return false;

		ASTClass astClass = (ASTClass) o;

		if (packageName != null ? !packageName.equals(astClass.packageName) : astClass.packageName != null)
			return false;
		if (getMethods() != null ? !getMethods().equals(astClass.getMethods()) : astClass.getMethods() != null)
			return false;
		if (getName() != null ? !getName().equals(astClass.getName()) : astClass.getName() != null) return false;
		if (getAccessRight() != astClass.getAccessRight()) return false;
		if (getImplmentsInterfaces() != null ? !getImplmentsInterfaces().equals(astClass.getImplmentsInterfaces()) : astClass.getImplmentsInterfaces() != null)
			return false;
		if (getExtendClass() != null ? !getExtendClass().equals(astClass.getExtendClass()) : astClass.getExtendClass() != null)
			return false;
		if (getLine() != astClass.getLine()) return false;
		if (getLineEnd() != astClass.getLineEnd()) return false;
		return true;
	}

	public String getPath(){
		return path;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean anInterface) {
		isInterface = anInterface;
	}

	@Override
	public int hashCode() {
		int result = packageName != null ? packageName.hashCode() : 0;
		result = 31 * result + (getMethods() != null ? getMethods().hashCode() : 0);
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		result = 31 * result + (getAccessRight() != null ? getAccessRight().hashCode() : 0);
		result = 31 * result + (getImplmentsInterfaces() != null ? getImplmentsInterfaces().hashCode() : 0);
		result = 31 * result + (getExtendClass() != null ? getExtendClass().hashCode() : 0);
		return result;
	}

	@Override
	public void visit(ASTVisitor visitor) {
		visitor.enterASTClass(this);
		for(IASTMethod m : methods){
			m.visit(visitor);
		}
		for(ASTImport i : imports){
			i.visit(visitor);
		}
		for(ASTAttribute a : attributes){
			a.visit(visitor);
		}
		visitor.exitASTClass(this);
	}

	public IASTMethod getMethodBySignature(String name, List<String> types){
		IASTMethod out = null;
		for(IASTMethod m : this.methods){
			if(!m.getName().equals(name)) continue;
			List<ASTVariable> pars = m.getParameters();
			if(pars.size() == types.size()){
				boolean flag = true;
				for(int i = 0; i < pars.size(); i++){
					if(!pars.get(i).getType().equals(types.get(i))){
						flag = false;
					}
				}
				if(flag){
					out = m;
				}
			}
		}
		return out;
	}
	public IASTMethod getFirstMethodByName(String name){
		IASTMethod out = null;
		for(IASTMethod m : this.methods){
			if(!m.getName().equals(name)) continue;
				out = m;
		}
		return out;
	}

	public boolean isSameClass(ASTClass _class) {
		return this.packageName.equals(_class.getPackageName()) && this.name.equals(_class.getName());
	}

	public String getPackageMethod(IASTMethod m) {
		for(IASTMethod localM : this.methods){
			if(localM.equalsBySignature(m))
				return this.packageName;
		}
		return this.parent != null ? this.parent.getPackageMethod(m) : this.packageName;
	}

	public String getClassNameMethod(IASTMethod m) {
		for(IASTMethod localM : this.methods){
			if(localM.equalsBySignature(m)) return this.name;
		}
		return this.parent != null ? this.parent.getClassNameMethod(m) : this.name;
	}

	public String getRealPackageName() {
		if(this.parent == null) return this.packageName;
		else return this.parent.getRealPackageName();
	}

	public List<String> getImportsAsString() {
		List<String> out = new ArrayList<>();
		for(ASTImport imp : this.imports){
			out.add(imp.getPackagename());
		}
		return out;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public boolean isAbstract(){
		return this.isAbstract;
	}

    public boolean hasMethod(IASTMethod key) {
		for(IASTMethod m : this.getMethods()){
			if(m == key)
				return true;
		}
		return false;
    }
}
