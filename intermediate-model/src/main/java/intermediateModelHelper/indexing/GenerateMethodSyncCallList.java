package intermediateModelHelper.indexing;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.*;
import intermediateModel.structure.expression.*;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexData;
import intermediateModelHelper.indexing.structure.IndexMethod;
import intermediateModelHelper.indexing.structure.SyncMethodCall;
import intermediateModelHelper.types.DataTreeType;
import intermediateModelHelper.types.ResolveTypes;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The idea is to parse the method that we are currently looking through and collect all the information about
 * what are the method calls to synchronized method.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class GenerateMethodSyncCallList extends ParseIM {

	ASTClass _class;
	ASTClass _lastClass;
	List<IASTMethod> _methods = new ArrayList<>();
	List<SyncMethodCall> syncCalls = new ArrayList<>();
	MongoConnector mongo;
	Map<String, List<IndexMethod>> syncMethods = new HashMap<>();
	Map<String, List<IndexMethod>> syncMethodsInt = new HashMap<>();
	List<IndexData> imports = new ArrayList<>();
	String lastMethod;
	List<String> lastSignature = new ArrayList<>();
	List<IndexData> newObjects = new ArrayList<>();

	/**
	 * Constructor.
	 * It creates the object and pre-process the {@link ASTClass} in input collecting from the database all the info
	 * from the import section.
	 * @param _class	Class under analysis
	 * @param _method	Method of the class under analysis
	 */
	public GenerateMethodSyncCallList(ASTClass _class, IASTMethod _method) {
		super(_class);
		this._class = _class;
		this._lastClass = _class;
		this._methods.add(_method);
		mongo = MongoConnector.getInstance(MongoOptions.getInstance().getDbName());
		processImports();

	}

	/**
	 * Constructor.
	 * It creates the object and pre-process the {@link ASTClass} in input collecting from the database all the info
	 * from the import section.
	 * @param _class	Class under analysis
	 * @param _methods	List of Methods of the class under analysis
	 */
	public GenerateMethodSyncCallList(ASTClass _class, List<IASTMethod> _methods) {
		super(_class);
		this._class = _class;
		this._methods = _methods;
		mongo = MongoConnector.getInstance(MongoOptions.getInstance().getDbName());
		processImports();

	}

	/**
	 * It connects to the mongodb and prepare the data in order to resolve the from which
	 * objects the method calls are coming from.
	 */
	private void processImports() {
		for(ASTImport imp : this._class.getImports()){
			String pkg = imp.getPackagename();
			List<IndexData> current = mongo.getFromImport(pkg);
			if(current.size() > 0) imports.addAll(current);
			//System.out.println(Arrays.toString(d.toArray()));
			for(IndexData index : current){
				String objName = index.getClassName();
				processImportClass(index, objName);
			}
			if(Character.isUpperCase(pkg.substring(pkg.lastIndexOf(".") + 1).charAt(0))){
				pkg = pkg + ".*";
				current = mongo.getFromImport(pkg);
				if(current.size() > 0) imports.addAll(current);
				//System.out.println(Arrays.toString(d.toArray()));
				for(IndexData index : current){
					String objName = index.getClassName();
					processImportClass(index, objName);
				}
			}
		}
		//plus files in my package
		String pkg = this._class.getRealPackageName() + ".*";
		List<IndexData> current = mongo.getFromImport(pkg);
		if(current.size() > 0) imports.addAll(current);
		//System.out.println(Arrays.toString(d.toArray()));
		for(IndexData index : current){
			String objName = index.getClassName();
			processImportClass(index,objName);
		}
	}

	private void processImportClass(IndexData index, String objName){
		List<IndexMethod> names = new ArrayList<>();
		for(IndexMethod m : index.getListOfSyncMethods()){
			names.add(m);
		}
		if(syncMethods.containsKey(objName)){
			names.addAll(syncMethods.get(objName));
		}
		syncMethods.put(objName, names);
		//is it an interface?
		if(index.isInterface()){
			List<IndexData> moreIndex = mongo.resolveClassImplementingInterface(index.getName(),index.getClassPackage());
			List<IndexMethod> syncMethodInt = new ArrayList<>();
			for(IndexData idx : moreIndex){
				for(IndexMethod m : idx.getListOfSyncMethods()){
					syncMethodInt.add(m);
				}
			}
			if(syncMethodsInt.containsKey(objName)){
				syncMethodInt.addAll(syncMethodsInt.get(objName));
			}
			syncMethodsInt.put(objName, syncMethodInt);
		}
		//now search on parent
		if(!index.getExtendedType().equals("Object")){
			String type = index.getExtendedType();
			boolean found = false;
			for(String imp : index.getImports()){
				if(imp.endsWith(type)){
					found = true;
					String pkg = imp.replace(type, "");
					if(pkg.endsWith("."))
						pkg = pkg.substring(0, pkg.length()-1);
					List<IndexData> parentIndex = mongo.getIndex(type, pkg);
					for(IndexData pIdx : parentIndex){
						processImportClass(pIdx, objName);
						processImportClass(pIdx, pIdx.getClassName());
					}
				}
			}
			if(!found){
				List<IndexData> parentIndex = mongo.getIndex(type, index.getClassPackage());
				for(IndexData pIdx : parentIndex){
					processImportClass(pIdx, objName);
					processImportClass(pIdx, pIdx.getClassName());
				}
				if(type.length() - type.replace(".","").length() > 2) {
					String t,p;
					p = type.substring(0, type.lastIndexOf("."));
					t = type.substring(type.lastIndexOf("." ) + 1);
					parentIndex = mongo.getIndex(t, p);
					for (IndexData pIdx : parentIndex) {
						processImportClass(pIdx, objName);
						processImportClass(pIdx, pIdx.getClassName());
					}
				}
			}
		}
	}

	private IndexData getFromImport(String type) {
		for(IndexData i : imports){
			if(i.getClassName().equals(type))
				return i;
		}
		return null;
	}

	private boolean containsMethod(String name, List<Pair<String,String>> parsType, List<IndexMethod> methods){
		for(IndexMethod m : methods){
			if(m.equalBySignature(name, parsType))
				return true;
		}
		return false;
	}
	private IndexMethod getMethod(String name, List<Pair<String,String>> parsType, List<IndexMethod> methods){
		for(IndexMethod m : methods){
			if(m.equalBySignature(name, parsType))
				return m;
		}
		return null;
	}

	/**
	 * Run the computation of the calls to a synchronized method
	 * @return	List of {@link SyncMethodCall}
	 */
	public List<SyncMethodCall> calculateSyncCallList(){
		syncCalls.clear();
		super.createBaseEnv(_class);
		for(IASTMethod m : this._methods){
			lastMethod = m.getName();
			lastSignature.clear();
			for(ASTVariable p : m.getParameters()){
				lastSignature.add(p.getType());
			}
			super.analyzeMethod(m);
		}
		return syncCalls;
	}


	@Override
	protected void analyzeASTNewObject(ASTNewObject stm, Env env) {
		IndexData d = getFromImport(stm.getTypeName());
		if(d != null){
			newObjects.add(d);
		}
	}

	/**
	 * We are only interested in ASTRE
	 * <ul>
	 *     <li><b>Attribute Access</b>: Check if the attribute name exists in the list of Sync methods and as well the method call in its list.</li>
	 *     <li><b>Variable</b>: Get the type of the variable, with it check if is in the list of Sync methods and as well the method call in its list.</li>
	 *     <li><b>Multiple Method call</b>: Get the type of the return of the previous call, with it check if is in the list of Sync methods and as well
	 *     									the method call in its list. Moreover, we do not need to process recursively the check because it does it for us the visitor.</li>
	 *     <li><b>null</b>: Check if the call to a local class method is sync or not.</li>
	 * </ul>
	 * @param r 	Statement
	 * @param env   Environment
	 */
	@Override
	protected void analyzeASTRE(ASTRE r, Env env) {
		if(r == null || r.getExpression() == null){
			return;
		}
		String inMethod = lastMethod;
		String inMethodPkg = _class.getPackageName();
		String inMethodClass = _class.getName();
		List<String> inSignature = new ArrayList<>();
		inSignature.addAll(lastSignature);
		r.getExpression().visit(new DefualtASTREVisitor(){

			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				String methodCalled = elm.getMethodName();
				IASTRE expr = elm.getExprCallee();
				List<Pair<String,String>> actual_pars = new ArrayList<Pair<String,String>>();
				for(IASTRE p : elm.getParameters()){
					Pair<String,String> exprType;
					if(p instanceof ASTMethodCall){
						exprType = ResolveTypes.getTypeMethodCall(imports, _class, (ASTMethodCall) p, env);
					} else {
						String t = env.getExprType(p, _class);
						if(t == null){
							t = "null";
						}
						if(t.equals("this")) { t = _class.getName(); }
						if(t.endsWith(".this")) { t = t.substring(0, t.lastIndexOf(".")); }
						exprType = new Pair<>(t, ResolveTypes.getExactImportPkgFromType(imports, t) );
					}
					actual_pars.add(exprType);
				}
				String pkg2 = _class.getPackageName();
				if(expr == null){
					localCall(methodCalled, actual_pars, pkg2, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
				if(expr instanceof ASTAttributeAccess){
					//String attribute = ((ASTAttributeAccess) expr).getAttributeName();
					String t = env.getExprType(expr, _class);
					if(t == null){
						t = "null";
					}
					if(t.equals("this")) { t = _class.getName(); }
					searchInParent(t,actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
				if(expr instanceof ASTLiteral){
					String varName = ((ASTLiteral) expr).getValue();
					literalAccess(methodCalled, actual_pars, pkg2, r, inMethod, inSignature, inMethodPkg, inMethodClass, varName, env);
				}
				if(expr instanceof ASTMethodCall){
					Pair<String,String> type = ResolveTypes.getTypeMethodCall(imports, _class, (ASTMethodCall) expr, env);
					methodAccess(methodCalled, actual_pars, type, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
				if(expr instanceof ASTCast){
					String typeName = ((ASTCast) expr).getType();
					String pkg = ResolveTypes.getImportPkgFromType(imports, typeName);
					Pair<String,String> type = new Pair<>(typeName, pkg);
					methodAccess(methodCalled, actual_pars, type, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
			}
		});
	}

	private String getParentType(String type){
		for(int i = 0; i < imports.size(); i++){
			if(imports.get(i).getClassName().equals(type)){
				return imports.get(i).getExtendedType();
			}
		}
		return "Object";
	}

	private IndexData getParentType(String type, List<IndexData> imports){
		for(int i = 0; i < imports.size(); i++){
			if(imports.get(i).getClassName().equals(type)){
				IndexData d = imports.get(i);
				String extType = d.isInterface() && d.getInterfacesImplemented().size() > 0 ? d.getInterfacesImplemented().get(0) : d.getExtendedType();
				IndexData parent = ResolveTypes.getPackageFromImportsString(d.getClassPackage(), d.getImports(), extType);
				if(parent != null)
					return parent;
			}
		}
		return null;
	}


	private void localCall(String methodCalled, List<Pair<String,String>> actual_pars, String pkgClass, ASTRE r, String inMethod, List<String> inSignature, String inMethodPkg, String inMethodClass){
		//local call - we can skip constructors because they are never sync
		boolean found = false;
		for(IASTMethod m : _class.getAllMethods()){
			if(m instanceof ASTMethod && m.getName().equals(methodCalled)){
				found = true;
				ASTMethod method = ((ASTMethod) m);
				if(method.isAbstract()){ //cannot use this :( we have to go through the implementations
					searchInExtension(_class.getPackageName(), _class.getName(), actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
				else if(method.isSynchronized()){
					boolean flag = true;
					if(actual_pars.size() == m.getParameters().size()){
						for(int i = 0, max = actual_pars.size(); i < max; i++){
							String pkg1 = actual_pars.get(i).getValue1();
							if(!DataTreeType.checkEqualsTypes(actual_pars.get(i).getValue0(), m.getParameters().get(i).getType(), pkg1, pkgClass)){
								flag = false;
							}
						}
					} else {
						flag = false;
					}
					if(flag) {
						List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
						for(int i = 0, max = m.getParameters().size(); i < max; i++){
							methodPars.add(new Pair<>( m.getParameters().get(i).getType(), _class.getPackageName() ));
						}
						//local call
						String pkg = _class.getRealPackageName();
						String name = _class.getClassNameMethod(m);
						syncCalls.add(new SyncMethodCall(pkg, name, methodCalled, m.getSignature(), r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, method.isStatic()));
					}
				}
			}
		}
		for(IndexData c : newObjects){
			if(containsMethod(methodCalled, actual_pars, c.getListOfSyncMethods())){
				//also the call is in the list -> we gotta a match
				IndexMethod m = getMethod(methodCalled, actual_pars, c.getListOfSyncMethods());
				if(m != null) {
					found = true;
					List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
					List<String> signature = new ArrayList<String>();
					for(int i = 0, max = m.getParameters().size(); i < max; i++){
						methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
						signature.add(m.getParameters().get(i).getType());
					}
					syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(), methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
				}
			}
		}
		//should we consider also the parent one? I suppose jup we have to do ;)
		if(!found){
			searchInParent(this._class.getExtendClass(), actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
			for(IndexData c : newObjects){
				searchInParent(c.getExtendedType(), actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
			}
		}
	}

	private void searchInExtension(String packageName, String name, List<Pair<String, String>> actual_pars, String methodCalled, ASTRE r, String inMethod, List<String> inSignature, String inMethodPkg, String inMethodClass) {
		List<IndexData> indexes = mongo.getExtensionOfClass(packageName, name);
		for(IndexData c : indexes){
			if(containsMethod(methodCalled, actual_pars, c.getListOfSyncMethods())){
				//also the call is in the list -> we gotta a match
				IndexMethod m = getMethod(methodCalled, actual_pars, c.getListOfSyncMethods());
				if(m != null) {
					List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
					List<String> signature = new ArrayList<String>();
					for(int i = 0, max = m.getParameters().size(); i < max; i++){
						methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
						signature.add(m.getParameters().get(i).getType());
					}
					syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(), methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
				}
			}
		}
	}

	private void literalAccess(String methodCalled, List<Pair<String,String>> actual_pars, String pkgClass, ASTRE r, String inMethod, List<String> inSignature, String inMethodPkg, String inMethodClass, String varName, Env env){
		if(varName.endsWith("this")){ //if is this. check locally
			for(IASTMethod m : _class.getAllMethods()){
				if(m instanceof ASTMethod){
					ASTMethod method = ((ASTMethod) m);
					if(method.isAbstract()){ //cannot use this :( we have to go through the implementations
						searchInExtension(_class.getPackageName(), _class.getName(), actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
					}
					else if(method.isSynchronized() && method.getName().equals(methodCalled)){
						boolean flag = true;
						if(actual_pars.size() == m.getParameters().size()){
							for(int i = 0, max = actual_pars.size(); i < max; i++){
								String pkg1 = actual_pars.get(i).getValue1();
								if(!DataTreeType.checkEqualsTypes(actual_pars.get(i).getValue0(), m.getParameters().get(i).getType(), pkg1, pkgClass)){
									flag = false;
								}
							}
						} else {
							flag = false;
						}
						if(flag) {
							List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
							for(int i = 0, max = m.getParameters().size(); i < max; i++){
								methodPars.add(new Pair<>( m.getParameters().get(i).getType(), _class.getPackageName() ));
							}
							String pkg = _class.getPackageMethod(m);
							String name = _class.getClassNameMethod(m);
							syncCalls.add(new SyncMethodCall(pkg, name, methodCalled, m.getSignature(), r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, method.isStatic()));
						}
					}
				}
			}
		} else if(varName.equals("super")) {
			//search in parent class
			String parentObjType = this._class.getExtendClass();
			searchInParent(parentObjType, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
		} else { //search from the env
			IASTVar var = env.getVar(varName);
			if(var != null){ //we have smth in the env (should be always the case)
				String varType = var.getTypeNoArray();
				boolean found = false;
				if(syncMethods.containsKey(varType)){
					//exists in the list
					List<IndexMethod> methods = syncMethods.get(varType);
					if(containsMethod(methodCalled, actual_pars, methods)){
						found = true;
						//also the call is in the list -> we gotta a match
						IndexMethod m = getMethod(methodCalled, actual_pars, methods);
						if(m != null) {
							List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
							List<String> signature = new ArrayList<String>();
							for(int i = 0, max = m.getParameters().size(); i < max; i++){
								methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
								signature.add(m.getParameters().get(i).getType());
							}
							syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(), methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
						}
					}
				}
				if(!found){
					searchInParent(varType, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
			} else {
				//static call
				IndexData c = getFromImport(varName);
				if(c != null){
					boolean found = false;
					if(containsMethod(methodCalled, actual_pars, c.getListOfSyncMethods())){
						found = true;
						//also the call is in the list -> we gotta a match
						IndexMethod m = getMethod(methodCalled, actual_pars, c.getListOfSyncMethods());
						if(m != null) {
							List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
							List<String> signature = new ArrayList<String>();
							for(int i = 0, max = m.getParameters().size(); i < max; i++){
								methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
								signature.add(m.getParameters().get(i).getType());
							}
							syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(), methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
						}
					}
					if(!found){
						searchInParent(varName, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
					}
				}
			}
		}
	}

	private void methodAccess(String methodCalled, List<Pair<String,String>> actual_pars, Pair<String,String> type, ASTRE r, String inMethod, List<String> inSignature, String inMethodPkg, String inMethodClass){
		MongoConnector mongo = MongoConnector.getInstance();
		List<IndexData> src = mongo.getIndex(type.getValue0(), type.getValue1());
		boolean found = false;
		if(src.size() > 0){
			for(IndexData d : src){
				for(IndexMethod m :d.getListOfSyncMethods()){
					if(m.equalBySignature(methodCalled, actual_pars)){
						found = true;
						List<Pair<String,String>> methodPars = new ArrayList<>();
						List<String> signature = new ArrayList<>();
						for(int i = 0, max = m.getParameters().size(); i < max; i++){
							methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
							signature.add(m.getParameters().get(i).getType());
						}
						syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(),  methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
					}
				}
			}
		}
		if(!found) {
			searchInParent(type.getValue0(), actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
		}
	}

	private void searchInParent(String parentType, List<Pair<String,String>> actual_pars, String methodCalled, ASTRE r, String inMethod, List<String> inSignature, String inMethodPkg, String inMethodClass ){
		boolean found = false;
		List<IndexData> parentImports = new ArrayList<>(imports);
		while(!found && !parentType.equals("Object")){
			if(syncMethods.containsKey(parentType)) {
				List<IndexMethod> methods = syncMethods.get(parentType);
				if(containsMethod(methodCalled, actual_pars, methods)){
					found = true;
					//also the call is in the list -> we gotta a match
					IndexMethod m = getMethod(methodCalled, actual_pars, methods);
					if(m != null) {
						List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
						List<String> signature = new ArrayList<String>();
						for(int i = 0, max = m.getParameters().size(); i < max; i++){
							methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
							signature.add(m.getParameters().get(i).getType());
						}
						syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(),  methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
					}
				} else {
					searchInInterface(parentType, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
					IndexData d = ResolveTypes.getPackageFromImports(parentImports, parentType);
					if(d != null) {
						parentImports.clear();
						for (String imp : d.getImports()) {
							parentImports.addAll(mongo.getFromImport(imp));
						}
						parentImports.addAll(mongo.getFromImport(d.getClassPackage() + ".*"));
						searchInExtension(d.getClassPackage(), parentType, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
					}
					IndexData parent = getParentType(parentType, parentImports);
					if (parent != null) {
						parentType = parent.getClassName();

						//parentImports.clear();
						for (String imp : parent.getImports()) {
							parentImports.addAll(mongo.getFromImport(imp));
						}
						for (IndexMethod m : parent.getListOfSyncMethods()) {
							if (m.equalBySignature(methodCalled, actual_pars)) {
								found = true;
								List<Pair<String, String>> methodPars = new ArrayList<Pair<String, String>>();
								List<String> signature = new ArrayList<String>();
								for (int i = 0, max = m.getParameters().size(); i < max; i++) {
									methodPars.add(new Pair<>(m.getParameters().get(i).getType(), m.getPackageName()));
									signature.add(m.getParameters().get(i).getType());
								}
								syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(), methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
							}
						}
					} else {
						parentType = getParentType(parentType);
					}
				}
			} else {
				searchInInterface(parentType, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				IndexData d = ResolveTypes.getPackageFromImports(imports, parentType);
				parentImports.clear();
				if(d != null) {
					parentImports.clear();
					for (String imp : d.getImports()) {
						parentImports.addAll(mongo.getFromImport(imp));
					}
					searchInExtension(d.getClassPackage(), parentType, actual_pars, methodCalled, r, inMethod, inSignature, inMethodPkg, inMethodClass);
				}
				IndexData parent = getParentType(parentType, parentImports);
				if(parent != null){
					parentType = parent.getClassName();
					MongoConnector mongo = MongoConnector.getInstance();
					for(String imp : parent.getImports()){
						parentImports.addAll(mongo.getFromImport(imp));
					}
					for(IndexMethod m : parent.getListOfSyncMethods()){
						if(m.equalBySignature(methodCalled, actual_pars)){
							found = true;
							List<Pair<String,String>> methodPars = new ArrayList<Pair<String, String>>();
							List<String> signature = new ArrayList<String>();
							for(int i = 0, max = m.getParameters().size(); i < max; i++){
								methodPars.add(new Pair<>( m.getParameters().get(i).getType(), m.getPackageName() ));
								signature.add(m.getParameters().get(i).getType());
							}
							syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(),  methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
						}
					}
				} else {
					parentType = getParentType(parentType);
				}
			}
		}
	}

	private void searchInInterface(String intfType, List<Pair<String,String>> actual_pars, String methodCalled, ASTRE r, String inMethod, List<String> inSignature, String inMethodPkg, String inMethodClass){
		//System.out.println("Search in the interfaces");
		if (syncMethodsInt.containsKey(intfType)) {
			//System.out.println("We are inside");
			List<IndexMethod> methods = syncMethodsInt.get(intfType);
			if (containsMethod(methodCalled, actual_pars, methods)) {
				//for each potential call we save a link
				for(IndexMethod m : methods){
					if(m.equalBySignature(methodCalled, actual_pars)){
						List<Pair<String, String>> methodPars = new ArrayList<>();
						List<String> signature = new ArrayList<>();
						for (int i = 0, max = m.getParameters().size(); i < max; i++) {
							methodPars.add(new Pair<>(m.getParameters().get(i).getType(), m.getPackageName()));
							signature.add(m.getParameters().get(i).getType());
						}
						syncCalls.add(new SyncMethodCall(m.getPackageName(), m.getFromClass(), methodCalled, signature, r, methodPars, inMethod, inSignature, inMethodPkg, inMethodClass, m.isStatic()));
					}
				}
			}
		}
	}
}
