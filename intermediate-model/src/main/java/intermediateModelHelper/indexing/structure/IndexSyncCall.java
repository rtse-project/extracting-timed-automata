package intermediateModelHelper.indexing.structure;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
@Entity("IndexSyncCall")
@Indexes(
		@Index(fields = @Field(value = "$**", type = IndexType.TEXT))
)
public class IndexSyncCall {
	@Id
	private ObjectId id;
	private String classPackage;
	private String name;
	private String methodName;
	private List<String> methodSignature;
	private String _inClassPackage;
	private String _inClassName;
	private String _inMethodName;
	private List<String> _signatureInMethod;
	private String path = "";
	private int line;
	private boolean isStatic;

	public IndexSyncCall() {
	}

	public IndexSyncCall(ObjectId id, String classPackage, String name, String methodName, List<String> methodSignature, String _inClassPackage, String _inClassName, String _inMethodName, List<String> _signatureInMethod, String path, int line, boolean isStatic) {
		this.id = id;
		this.classPackage = classPackage;
		this.name = name;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this._inClassPackage = _inClassPackage;
		this._inClassName = _inClassName;
		this._inMethodName = _inMethodName;
		this._signatureInMethod = _signatureInMethod;
		this.path = path;
		this.line = line;
		this.isStatic = isStatic;
	}

	public IndexSyncCall(SyncMethodCall call, String path) {
		this._inClassPackage = call.get_inPackageName();
		this._inClassName = call.get_inClassName();
		this.classPackage = call.get_packageName();
		this.name = call.get_className();
		this.methodName = call.get_methodCalled();
		this._inMethodName = call.get_inMethodName();
		this._signatureInMethod = call.get_signatureInMethod();
		this.path = path;
		this.line = call.getNode().getLine();
		this.methodSignature = call.get_signatureMethodCalled();
		this.isStatic = call.isStatic();
	}

	public List<String> getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(List<String> methodSignature) {
		this.methodSignature = methodSignature;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getPackageName() {
		return classPackage;
	}

	public void setPackageName(String classPackage) {
		this.classPackage = classPackage;
	}

	public String getClassName() {
		return name;
	}

	public void setClassName(String name) {
		this.name = name;
	}

	public String getInMethodName() {
		return _inMethodName;
	}

	public void setInMethodName(String _inMethodName) {
		this._inMethodName = _inMethodName;
	}

	public List<String> getSignatureInMethod() {
		return _signatureInMethod;
	}

	public void setSignatureInMethod(List<String> _signatureInMethod) {
		this._signatureInMethod = _signatureInMethod;
	}

	public String getClassPackage() {
		return classPackage;
	}

	public void setClassPackage(String classPackage) {
		this.classPackage = classPackage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String get_inClassPackage() {
		return _inClassPackage;
	}

	public void set_inClassPackage(String _inClassPackage) {
		this._inClassPackage = _inClassPackage;
	}

	public String get_inClassName() {
		return _inClassName;
	}

	public void set_inClassName(String _inClassName) {
		this._inClassName = _inClassName;
	}

	public String get_inMethodName() {
		return _inMethodName;
	}

	public void set_inMethodName(String _inMethodName) {
		this._inMethodName = _inMethodName;
	}

	public List<String> get_signatureInMethod() {
		return _signatureInMethod;
	}

	public void set_signatureInMethod(List<String> _signatureInMethod) {
		this._signatureInMethod = _signatureInMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setStatic(boolean synchronize) {
		isStatic = synchronize;
	}

	public boolean isStatic() {
		return isStatic;
	}
}
