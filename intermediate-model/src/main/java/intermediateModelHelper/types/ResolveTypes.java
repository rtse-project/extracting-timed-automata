package intermediateModelHelper.types;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.ASTAttribute;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTImport;
import intermediateModel.structure.expression.ASTAttributeAccess;
import intermediateModel.structure.expression.ASTLiteral;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.structure.IndexData;
import intermediateModelHelper.indexing.structure.IndexMethod;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ResolveTypes {

	private static Map<Triplet<String,List<String>,String>, List<IndexData>> cacheImport = new HashMap();

	public static IndexData getPackageFromImports(List<IndexData> imports, String type){
		for(IndexData imp : imports){
			if(imp.getClassName().equals(type))
				return imp;
		}
		//we can import a subclass for importing as well the parent one .-. apparently
		for(IndexData imp : imports){
			if(type == null){
				continue;
			}
			if(imp.getClassPackage().endsWith("." + type))
				return imp;
		}
		return null;
	}

	public static IndexData getPackageFromImportsString(String pkgClass, List<String> imports, String type){
		Triplet<String,List<String>,String> p = new Triplet<>(pkgClass,imports,type);
		if(cacheImport.containsKey(p)){
			return getPackageFromImports( cacheImport.get(p), type);
		}
		MongoConnector mongo = MongoConnector.getInstance();
		List<IndexData> out = new ArrayList<>();
		for(String pkg : imports){
			List<IndexData> current = mongo.getFromImport(pkg);
			if(current.size() > 0) out.addAll(current);
		}
		//plus files in my package
		String pkg = pkgClass + ".*";
		List<IndexData> current = mongo.getFromImport(pkg);
		if(current.size() > 0) out.addAll(current);
		cacheImport.put(p, out);
		return getPackageFromImports(out, type);
	}

	public static String getImportPkgFromType(List<IndexData> imports, String s) {
		String out = null;
		for(IndexData i : imports){
			DataTreeType tImp = null;
			try {
				tImp = new DataTreeType(i.getClassName(), i.getClassPackage());
				if(tImp.isTypeCompatible(s)){
					out = i.getClassPackage();
				}
			} catch (Exception e) {
				continue;
			}
		}
		return out;
	}

	public static String getExactImportPkgFromType(List<IndexData> imports, String s) {
		String out = "";
		for(IndexData i : imports){
			if(i.getClassName().equals(s))
				return i.getClassPackage();
		}
		return out;
	}

	/**
	 * Resolve the type of a method call.
	 * Possible cases:
	 * <ul>
	 *     <li><b>Attribute access ( Class.method() )</b>: search in the imports the class that has that method</li>
	 *     <li><b>Variable ( var.method() )</b>: <ul>
	 *         <li><b>this</b>: search the method in the current class</li>
	 *         <li><b>variable</b>: get the variable type from the {@link Env} and get the method from its class</li>
	 *     </ul></li>
	 *     <li><b>Method call ( methodn()._.method1().method0() )</b>: Resolve the type of the last call recursively and then search for the method definition in the class of the last type</li>
	 * </ul>
	 * @param expr	Expression which contains the method call to resolve the type
	 * @return		Name of the type or null if we cannot solve it
	 */
	public static Pair<String,String> getTypeMethodCall(List<IndexData> imports, ASTClass _class, ASTMethodCall expr, Env e){
		IASTRE calee = expr.getExprCallee();
		if(calee == null){
			//local method call
			calee = new ASTLiteral(-1,-1, "this");
		}
		String methodCalled = expr.getMethodName();
		List<Pair<String,String>> actual_pars = new ArrayList<Pair<String,String>>();
		for(IASTRE p : expr.getParameters()){
			Pair<String,String> exprType;
			if(p instanceof ASTMethodCall){
				exprType = ResolveTypes.getTypeMethodCall(imports, _class, (ASTMethodCall) p, e);
			} else {
				String type = e.getExprType(p, _class);
				exprType = new Pair<>( type ,ResolveTypes.getImportPkgFromType(imports, type) );
			}
			actual_pars.add( exprType );
		}
		if(calee instanceof ASTAttributeAccess){
			final String[] varTypeHelper = new String[1];
			((ASTAttributeAccess) calee).getVariableName().visit(new DefualtASTREVisitor(){
				@Override
				public void enterASTLiteral(ASTLiteral elm) {
					varTypeHelper[0] = elm.getValue();
				}
			});
			String varType = varTypeHelper[0];
			if(varType != null && (varType.equals("this") || Character.isLowerCase(varType.charAt(0))) ){
				String varName = ((ASTAttributeAccess) calee).getAttributeName();
				IASTVar var = e.getVar(varName);
				if(var != null){
					//exist smth in the env (should be always the case)
					IndexData _classImport = ResolveTypes.getPackageFromImports(imports,var.getType());
					if(_classImport != null){
						//we have smth to work with
						for(IndexMethod m : _classImport.getListOfMethods()){
							if(m.equalBySignature(methodCalled, actual_pars)){
								String type = m.getReturnType();
								IndexData imp = ResolveTypes.getPackageFromImportsString(_classImport.getClassPackage(), _classImport.getImports(), type);
								String pkg;
								if(imp == null){
									pkg = _classImport.getClassPackage();
								} else {
									pkg = imp.getClassPackage();
								}
								return new Pair<>(type, pkg);
							}
						}
					}
				} else {
					//static call?
					IndexData _classImport = ResolveTypes.getPackageFromImports(imports, varName);
					if(_classImport != null){
						for(IndexMethod m : _classImport.getListOfMethods()){
							if(m.equalBySignature(methodCalled, actual_pars)){
								String type = m.getReturnType();
								IndexData imp = ResolveTypes.getPackageFromImports(imports, type);
								String pkg;
								if(imp == null){
									pkg = _classImport.getClassPackage();
								} else {
									pkg = imp.getClassPackage();
								}
								return new Pair<>(type, pkg);
							}
						}
					}
				}
			} else {
				IndexData _class_pkg = ResolveTypes.getPackageFromImports(imports,varType);
				if(_class_pkg != null){
					//we have smth to work with
					for(IndexMethod m : _class_pkg.getListOfMethods()){
						if(m.equalBySignature(methodCalled, actual_pars)){
							return new Pair<>(m.getReturnType(), _class_pkg.getClassPackage());
						}
					}
				}
			}
		}
		if(calee instanceof ASTLiteral){
			String varName = ((ASTLiteral) calee).getValue();
			if(varName.equals("this")){
				String type = localSearch(_class, methodCalled, actual_pars, imports);
				IndexData imp = ResolveTypes.getPackageFromImports(imports, type);
				String pkg;
				//local search
				if(imp == null){
					//basic type
					pkg = "java.lang";
				} else {
					pkg = imp.getClassPackage();
				}
				return new Pair<>(type, pkg);
			} else {
				//get type in env
				IASTVar var = e.getVar(varName);
				if(var != null){
					//exist smth in the env (should be always the case)
					IndexData _classImport = ResolveTypes.getPackageFromImports(imports,var.getType());
					if(_classImport != null){
						//we have smth to work with
						for(IndexMethod m : _classImport.getListOfMethods()){
							if(m.equalBySignature(methodCalled, actual_pars)){
								String type = m.getReturnType();
								IndexData imp = ResolveTypes.getPackageFromImports(imports, type);
								String pkg;
								if(imp == null){
									pkg = "";
								} else {
									pkg = imp.getClassPackage();
								}
								return new Pair<>(type, pkg);
							}
						}
					}
				} else {
					//static call?
					IndexData _classImport = ResolveTypes.getPackageFromImports(imports, varName);
					if(_classImport != null){
						for(IndexMethod m : _classImport.getListOfMethods()){
							if(m.equalBySignature(methodCalled, actual_pars)){
								String type = m.getReturnType();
								IndexData imp = ResolveTypes.getPackageFromImports(imports, type);
								String pkg;
								if(imp == null){
									pkg = _classImport.getClassPackage();
								} else {
									pkg = imp.getClassPackage();
								}
								return new Pair<>(type, pkg);
							}
						}
					}
				}
			}
		}
		if(calee instanceof ASTMethodCall){
			//recursive call
			Pair<String,String> previousCallReturn = getTypeMethodCall(imports, _class, ((ASTMethodCall) calee), e);
			IndexData _classImport = ResolveTypes.getPackageFromImports(imports,previousCallReturn.getValue0());
			if(_classImport != null){
				//we have smth to work with
				for(IndexMethod m : _classImport.getListOfMethods()){
					if(m.equalBySignature(methodCalled, actual_pars)){
						String type = m.getReturnType();
						String pkg = "";
						IndexData d = getPackageFromImportsString(_classImport.getClassPackage(), _classImport.getImports(), type);
						if(d == null){
							pkg = _classImport.getClassPackage();
						} else {
							pkg = d.getClassPackage();
						}
						return new Pair<>(type, pkg);
					}
				}
				//if not found search in parent
				return searchInParent(previousCallReturn, methodCalled,actual_pars);
			} else {
				MongoConnector mongo = MongoConnector.getInstance();
				//it is possible that it is in the same package of the previous method call
				List<IndexData> src = mongo.getIndex(previousCallReturn.getValue0(), previousCallReturn.getValue1());
				if(src.size() > 0){
					for(IndexData d : src){
						for(IndexMethod m :d.getListOfMethods()){
							if(m.equalBySignature(methodCalled, actual_pars)){
								String type = m.getReturnType();
								String pkg = "";
								IndexData data = getPackageFromImportsString(d.getClassPackage(), d.getImports(), type);
								if(data == null){
									pkg = d.getClassPackage();
								} else {
									pkg = data.getClassPackage();
								}
								return new Pair<>(type, pkg);
							}
						}
					}
				}
				//could be a local call then
				for (IASTMethod m : _class.getMethods()) {
					if (m.equalsBySignature(_class.getPackageMethod(m), methodCalled, actual_pars)) {
						return new Pair<>(m.getReturnType(), _class.getPackageName());
					}
				}
			}
		}
		return new Pair<>("Object", "java.lang");
	}

	private static Pair<String,String> searchInParent(Pair<String, String> previousCallReturn, String methodCalled, List<Pair<String, String>> actual_pars) {
		MongoConnector mongo = MongoConnector.getInstance();
		String type = previousCallReturn.getValue0();
		String pkg = previousCallReturn.getValue1();
		while(!type.equals("Object")) {
			List<IndexData> src = mongo.getIndex(type, pkg);
			if (src.size() > 0) {
				for (IndexData d : src) {
					for (IndexMethod m : d.getListOfMethods()) {
						if (m.equalBySignature(methodCalled, actual_pars)) {
							String t = m.getReturnType();
							IndexData data = getPackageFromImportsString(d.getClassPackage(), d.getImports(), type);
							if (data == null) {
								pkg = d.getClassPackage();
							} else {
								pkg = data.getClassPackage();
							}
							return new Pair<>(t, pkg);
						}
					}
					IndexData ext;
					if(d.isInterface() && d.getInterfacesImplemented().size() > 0){
						ext = getPackageFromImportsString(d.getClassPackage(), d.getImports(), d.getInterfacesImplemented().get(0));
					} else {
						ext = getPackageFromImportsString(d.getClassPackage(), d.getImports(), d.getExtendedType());
					}
					if(ext == null){
						type = "Object";
					} else {
						type = ext.getClassName();
						pkg = ext.getClassPackage();
					}
				}

			} else {
				type = "Object";
			}
		}
		return new Pair<>("Object", "java.lang");
	}

	private static String localSearch(ASTClass _class, String methodCalled, List<Pair<String,String>> actual_pars, List<IndexData> imports){
		for(IASTMethod m : _class.getMethods()){
			if(m.equalsBySignature(_class.getPackageMethod(m), methodCalled, actual_pars)){
				return m.getReturnType();
			}
		}
		if(_class.getParent() != null)
			return localSearch(_class.getParent(), methodCalled, actual_pars, imports);
		else {
			//does it extends smth
			String type = _class.getExtendClass();
			List<IndexData> parentImports = new ArrayList<>(imports);
			MongoConnector mongo = MongoConnector.getInstance();
			while(!type.equals("Object")){
				IndexData p = getPackageFromImports(parentImports, type);
				if(p == null){
					return "";
				}
				for(IndexMethod m : p.getListOfMethods()){
					if(m.equalBySignature(methodCalled, actual_pars)){
						return m.getReturnType();
					}
				}
				//go through the parent
				parentImports.clear();
				for (String imp : p.getImports()) {
					parentImports.addAll(mongo.getFromImport(imp));
				}
				parentImports.addAll(mongo.getFromImport(p.getClassPackage() + ".*"));
				type = p.getExtendedType();
			}
			return "";
		}

	}

	public static Pair<String,String> getSynchronizedExprType(List<IndexData> imports, IASTRE expr, ASTClass c, Env e){
		//5 cases: this, var, methodCall(), Smth.class, Smth.attribute
		Pair<String,String> out = new Pair<>("","");
		//cases: this, var, Smth.class, ClassName.this
		if(expr instanceof ASTLiteral){
			String val = ((ASTLiteral) expr).getValue();
			if(val.equals("this") || val.endsWith(".this")) {
				out = new Pair<>(c.getPackageName(), c.getName());
			}
			else if(val.endsWith(".class")){
				String class_name = val.substring(0, val.indexOf(".class"));
				if(class_name.equals(c.getName())){
					out = new Pair<>(c.getPackageName(), c.getName());
				} else {
					IndexData d = getPackageFromImports(imports, class_name);
					if(d == null){
						//if d is null then it is a normal object
						out = new Pair<>("java.lang", class_name);
					} else {
						out = new Pair<>(d.getClassPackage(), d.getClassName());
					}
				}
			}
			else {
				//it is a variable
				IASTVar type = e.getVar(val);
				if(type == null){
					//System.err.println("[Var] Should be not the case! Resolving sync expr for class:" + c.getPath());
					//System.err.println("@line:" + expr.getLine());
				} else {
					IndexData d = getPackageFromImports(imports, type.getType());
					if(d == null){
						//if d is null then it is a normal object
						out = new Pair<>("java.lang", type.getType());
					} else {
						out = new Pair<>(d.getClassPackage(), d.getClassName());
					}
				}
			}
		}
		//cases: Smth.attribute
		if(expr instanceof ASTAttributeAccess){
			IASTRE var = ((ASTAttributeAccess) expr).getVariableName();
			String attribute = ((ASTAttributeAccess) expr).getAttributeName();
			if (var instanceof ASTLiteral) {
				String varName = ((ASTLiteral) var).getValue();
				if(varName.equals("this")){
					varName = attribute;
				}
				IASTVar type = e.getVar(varName);
				if (type == null) {
					//System.err.println("[Attribute] Should be not the case! Resolving sync expr for class:" + c.getPath());
					//System.err.println("@line:" + ((ASTAttributeAccess) expr).getLine() );
				} else {
					IndexData d = getPackageFromImports(imports, type.getType());
					if (d == null) {
						//if d is null then it is a standard obj
						out = new Pair<>("java.lang", type.getType());
					} else {
						List<ASTClass> classes = JDTVisitor.parse(d.getPath());
						for(ASTClass cc : classes){
							if(cc.getName().equals(type.getType())){
								for(ASTAttribute a : cc.getAttributes()){
									if(a.getName().equals(attribute)){
										out = getType(a.getType(),cc);
									}
								}
							}
						}

					}
				}
			}
		}
		//cases: methodCall()
		if(expr instanceof ASTMethodCall){
			Pair<String,String> type = getTypeMethodCall(imports, c, (ASTMethodCall) expr, e);
			out = new Pair<>(type.getValue1(), type.getValue0());
		}
		return out;
	}

	public static Pair<String,String> getType(String type, ASTClass c){
		List<IndexData> imports = new ArrayList<>();
		Pair<String,String> out = new Pair<>("","");
		for(ASTImport i : c.getImports()){
			imports.addAll(MongoConnector.getInstance().getFromImport(i.getPackagename()));
		}
		IndexData d = getPackageFromImports(imports, type);
		if(d == null){
			//if d is null then it is a normal object
			out = new Pair<>("java.lang", type);
		} else {
			out = new Pair<>(d.getClassPackage(), d.getClassName());
		}
		return out;
	}

	public static String getAttributeType(String attributeClass, String type, ASTClass c){
		if(attributeClass.endsWith(".this"))
			attributeClass = attributeClass.substring(0, attributeClass.lastIndexOf("."));
		List<String> imports = new ArrayList<>();
		for(ASTImport imp : c.getImports()){
			String pkg = imp.getPackagename();
			imports.add(pkg);
		}
		IndexData data = getPackageFromImportsString(c.getRealPackageName(), imports, attributeClass);
		if(data != null){
			List<ASTClass> ccList = JDTVisitor.parse(data.getPath());
			for(ASTClass cc : ccList){
				for(ASTAttribute a : cc.getAttributes()){
					if(a.getName().equals(type)){
						return a.getType();
					}
				}
			}
		}
		return attributeClass;
	}
}