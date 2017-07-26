package intermediateModelHelper.indexing.structure;

import intermediateModel.structure.ASTRE;
import intermediateModelHelper.types.DataTreeType;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class SyncMethodCall {
	private String _packageName;
	private String _className;
	private List<String> _signatureMethodCalled = new ArrayList<>();
	private String _inMethodName;
	private List<String> _signatureInMethod;
	private String _methodCalled;
	private List<Pair<String, String>> paramsType = new ArrayList<>();
	private ASTRE node;
	private String _inPackageName;
	private String _inClassName;
	private boolean isStatic;

	public static final short _SYNC_CALL_NORMAL_ = 0;
	public static final short _SYNC_CALL_MAYBE_ = 1;

	public SyncMethodCall(String _packageName, String _className, String _methodCalled, List<String> signatureMethodCalled, ASTRE node, List<Pair<String, String>> paramsType, String _inMethodName, List<String> _signatureInMethod, String _inPackageName, String _inClassName, boolean isStatic) {
		this._packageName = _packageName;
		this._className = _className;
		this._methodCalled = _methodCalled;
		this.node = node;
		this.paramsType = paramsType;
		this._inMethodName = _inMethodName;
		this._signatureInMethod = _signatureInMethod;
		this._signatureMethodCalled = signatureMethodCalled;
		this._inPackageName = _inPackageName;
		this._inClassName = _inClassName;
		this.isStatic = isStatic;
	}

	public List<String> get_signatureMethodCalled() {
		return _signatureMethodCalled;
	}

	public String get_inMethodName() {
		return _inMethodName;
	}

	public List<String> get_signatureInMethod() {
		return _signatureInMethod;
	}

	public List<Pair<String, String>> getParamsType() {
		return paramsType;
	}

	public String get_packageName() {
		return _packageName;
	}

	public String get_className() {
		return _className;
	}

	public String get_methodCalled() {
		return _methodCalled;
	}

	@Override
	public String toString() {
		return _packageName + "." + _className + "#" + _methodCalled;
	}

	public ASTRE getNode() {
		return node;
	}

	public String get_inPackageName() {
		return _inPackageName;
	}

	public String get_inClassName() {
		return _inClassName;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SyncMethodCall)) return false;

		SyncMethodCall that = (SyncMethodCall) o;

		if (get_packageName() != null ? !get_packageName().equals(that.get_packageName()) : that.get_packageName() != null)
			return false;
		if (get_className() != null ? !get_className().equals(that.get_className()) : that.get_className() != null)
			return false;
		return get_methodCalled() != null ? get_methodCalled().equals(that.get_methodCalled()) : that.get_methodCalled() == null;
	}

	public boolean equalsBySignature(SyncMethodCall o){
		if(!this._packageName.equals(o.get_packageName())) return false;//not of the same pkg
		if(!this._className.equals(o.get_className())) return false;//not of the same class
		if(!this._methodCalled.equals(o.get_methodCalled())) return false; //not same method name
		if(this.paramsType.size() != o.getParamsType().size()) return false; //not same number of parameters
		for(int i = 0, max = this.paramsType.size(); i < max; i++){ //not same type for each par
			String type1 = this.paramsType.get(i).getValue0();
			String type2 = o.getParamsType().get(i).getValue0();
			String pkg1  = this.paramsType.get(i).getValue1();
			String pkg2  = o.getParamsType().get(i).getValue1();
			if(!DataTreeType.checkEqualsTypes(type1, type2, pkg1, pkg2)){
				return false;
			}
		}
		return true;
	}
}
