package PCFG.converter;

import PCFG.optimization.OptimizeForXAL;
import PCFG.structure.CFG;
import PCFG.structure.PCFG;
import PCFG.structure.anonym.AnonymClass;
import PCFG.structure.edge.AnonymEdge;
import PCFG.structure.edge.Edge;
import PCFG.structure.edge.IEdge;
import PCFG.structure.edge.SyncEdge;
import PCFG.structure.node.INode;
import PCFG.structure.node.Node;
import PCFG.structure.node.SyncNode;
import XAL.XALStructure.XALAddState;
import XAL.XALStructure.exception.XALMalformedException;
import XAL.XALStructure.items.*;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.ASTAttribute;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTImport;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTAttributeAccess;
import intermediateModel.structure.expression.ASTCast;
import intermediateModel.structure.expression.ASTLiteral;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.heuristic.definition.UndefiniteTimeout;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexData;
import intermediateModelHelper.indexing.structure.IndexMethod;
import intermediateModelHelper.types.ResolveTypes;
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
public class ToXAL implements IConverter {

	class FindMethodCall extends ParseIM {
		MongoConnector mongo;
		Map<String, List<IndexMethod>> methods = new HashMap<>();
		List<IndexData> imports = new ArrayList<>();
		List<Triplet<String,String, ASTRE>> methodCalls = new ArrayList<>();

		public FindMethodCall(ASTClass _class) {
			super(_class);
			//mongo = MongoConnector.getInstance(MongoOptions.getInstance().getDbName());
			//processImports();
		}

		/**
		 * Run the computation of the calls to a synchronized method
		 * @return	List
		 */
		public List<Triplet<String,String, ASTRE>> getMethodCalls(){
			methodCalls.clear();
			super.createBaseEnv(_class);
			for(IASTMethod m : this._class.getMethods()){
				super.analyzeMethod(m);
			}
			return methodCalls;
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
			for(IndexMethod m : index.getListOfMethods()){
				names.add(m);
			}
			if(methods.containsKey(objName)){
				names.addAll(methods.get(objName));
			}
			methods.put(objName, names);
			//now search on parent
			if(!index.getExtendedType().equals("Object")){
				String type = index.getExtendedType();
				boolean found = false;
				for(String imp : index.getImports()){
					if(imp.endsWith(type)){
						found = true;
						List<IndexData> parentIndex = mongo.getIndex(type, imp);
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

		@Override
		protected void analyzeASTRE(ASTRE r, Env env) {
			if(r == null || r.getExpression() == null){
				return;
			}
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
						localCall(methodCalled, actual_pars, pkg2, r);
					}
					if(expr instanceof ASTAttributeAccess){
						//String attribute = ((ASTAttributeAccess) expr).getAttributeName();
						String t = env.getExprType(expr, _class);
						if(t == null){
							t = "null";
						}
						if(t.equals("this")) { t = _class.getName(); }
						searchInParent(t,actual_pars, methodCalled, r);
					}
					if(expr instanceof ASTLiteral){
						String varName = ((ASTLiteral) expr).getValue();
						literalAccess(methodCalled, actual_pars, pkg2, r, varName, env);
					}
					if(expr instanceof ASTMethodCall){
						Pair<String,String> type = ResolveTypes.getTypeMethodCall(imports, _class, (ASTMethodCall) expr, env);
						methodAccess(methodCalled, actual_pars, type, r);
					}
					if(expr instanceof ASTCast){
						String typeName = ((ASTCast) expr).getType();
						String pkg = ResolveTypes.getImportPkgFromType(imports, typeName);
						Pair<String,String> type = new Pair<>(typeName, pkg);
						methodAccess(methodCalled, actual_pars, type, r);
					}
				}
			});
		}

		private void methodAccess(String methodCalled, List<Pair<String, String>> actual_pars, Pair<String, String> type, ASTRE r) {

		}

		private void literalAccess(String methodCalled, List<Pair<String, String>> actual_pars, String pkg2, ASTRE r, String varName, Env env) {
			if(varName.endsWith("this")){ //if is this. check locally
				localCall(methodCalled, actual_pars, pkg2, r);
			} else if(varName.equals("super")) {
				//search in parent class
			} else { //search from the env
				IASTVar var = env.getVar(varName);
				if(var != null){ //we have smth in the env (should be always the case)
					String varType = var.getTypeNoArray();
					if(methods.containsKey(varType)){
						//exists in the list
						List<IndexMethod> ms = methods.get(varType);
						if(containsMethod(methodCalled, actual_pars, ms)){
							//also the call is in the list -> we gotta a match
							IndexMethod m = getMethod(methodCalled, actual_pars, ms);
							if(m != null) {
								System.out.println("@" + r.getLine() + " -- " + m.getPackageName() + "." + m.getFromClass() + "::" + methodCalled);
								methodCalls.add(new Triplet<>(
										m.getPackageName() + "." + m.getFromClass(),
										methodCalled,
										r
								));
							}
						}
					}
				} else {
					//static call
					IndexData c = getFromImport(varName);
					if(c != null){
						if(containsMethod(methodCalled, actual_pars, c.getListOfSyncMethods())){
							//also the call is in the list -> we gotta a match
							IndexMethod m = getMethod(methodCalled, actual_pars, c.getListOfMethods());
							if(m != null) {
								System.out.println("@" + r.getLine() + " -- " + m.getPackageName() + "." + m.getFromClass() + "::" + methodCalled);
								methodCalls.add(new Triplet<>(
										m.getPackageName() + "." + m.getFromClass(),
										methodCalled,
										r
								));
							}
						}
					}
				}
			}
		}

		private void searchInParent(String t, List<Pair<String, String>> actual_pars, String methodCalled, ASTRE r) {

		}

		private void localCall(String methodCalled, List<Pair<String, String>> actual_pars, String pkg2, ASTRE r) {
			for(IASTMethod m : _class.getAllMethods()){
				if(m.equalsBySignature(pkg2, methodCalled, actual_pars)){
					//System.out.println("@" + r.getLine() + " -- " + c.getPackageName().replace(".","_") + "_" + c.getName() + "::" + methodCalled);
					methodCalls.add(new Triplet<>(
							c.getPackageName() + "." + c.getName(),
							methodCalled,
							r
					));
				}
			}
		}
	}

	XALDocument doc = null;
	XALAddState aut = null;
	XALAutomaton lastAutomaton = null;
	XALGlobalState globalVars = new XALGlobalState();

	List<ASTAttribute> attribute;
	ASTClass c;
	List<Triplet<String,String, ASTRE>> calls = new ArrayList<>();

	public ToXAL(ASTClass c) {
		this.c = c;
		this.attribute = c.getAttributes();
		FindMethodCall find = new FindMethodCall(this.c);
		//calls = find.getMethodCalls();
	}

	@Override
	public String convert(PCFG pcfg) {
		String name = "";
		for(CFG c : pcfg.getCFG()){
			name +=  c.getName() + "_";
		}
		return getXAL(pcfg,name).toString();
	}

	public XALDocument getXAL(PCFG pcfg, String filename) {
		pcfg = pcfg.optimize( new OptimizeForXAL() );
		XALDocument document = new XALDocument(filename);

		List<String> timeVars = new ArrayList<>();
		for(Node n : pcfg.getV()){
			for(Pair<String,String> var : n.getResetVars()){
				if(!timeVars.contains(var.getValue0())){
					timeVars.add(var.getValue0());
				}
			}
		}

		List<Pair<String,String>> vars = (List<Pair<String,String>>) pcfg.getAnnotation().get(String.valueOf(PCFG.DefaultAnnotation.GlobalVars));
		for(Pair<String,String> v : vars){
			XALVariable var = new XALVariable(v.getValue1(),v.getValue0(),"");
			globalVars.addVariable(var);
		}
		this.doc = document;
		for(CFG c : pcfg.getCFG()){
			getXAL(c);
		}
		/*for(SyncEdge sEdge : pcfg.getESync()){

		}*/
		//add method call
		for(String v : timeVars){
			XALCLockVariable cv = new XALCLockVariable(v);
			for(XALAutomaton a : document.getAutomatons()){
				a.getClocks().addVariable(cv);
			}
		}
		return document;
	}


	private void getXAL(CFG cfg){
		getXAL(cfg, cfg.getName());
	}

	private void getXAL(CFG cfg, String name) {
		XALAutomaton automa = new XALAutomaton(name);
		automa.setGlobalState(globalVars);
		doc.addAutomaton(automa);
		lastAutomaton = automa;
		aut = automa;
		for(Node v : cfg.getV()){
			getXAL(v);
		}
		if(aut.getStates().size() == 0){
			System.err.println("[DEBUG] No state detected for " + name + ". Switch on empty strategy");
			XALState s = new XALState("empty");
			s.setNumericID(0);
			aut.addState(s);
			lastAutomaton.addFinalState(s);
		}
		for(SyncNode s : cfg.getSyncNodesNoSubClass()){
			getXAL(s);
		}
		for(IEdge e : cfg.getE()){
			getXAL(e);
		}
		for(AnonymClass a : cfg.getAnonNodes()){
			getXAL(a);
		}
		/*
		for(AnonymEdge ae : cfg.getAnonEdge()){
			getXAL(ae);
		}*/
		//automa.addFinalState();
		//add vars.
		/*for (ASTAttribute a : attribute) {
			lastAutomaton.getGlobalState().addVariable(
					new XALVariable(a.getName(), a.getType(), /*a.getExpr() != null ? a.getExpr().getCode() :  "", XALVariable.XALIO.I)
			);
		}*/
	}

	private void getXAL(AnonymClass a) {
		for(CFG c : a.getCFG()){
			XALAutomaton bck = lastAutomaton;
			XALAddState bckAut = aut;
			getXAL(c, c.getName() + "_" + a.hashCode());
			lastAutomaton = bck;
			aut = bckAut;
		}
	}

	private void getXAL(INode s) {
		if(s instanceof Node){
			getXAL((Node)s);
		} else {
			getXAL((SyncNode)s);
		}
	}

	private void getXAL(Node v) {
		XALState s = new XALState(v.getNameNoID());
		s.setNumericID(v.getID());
		aut.addState(s);
		if(v.getConstraint() != null){

		}
		if(v.isStart()){
			try {
				lastAutomaton.setInitialState(s);
			} catch (XALMalformedException e) {
				e.printStackTrace();
			}
		}
		if(v.isEnd()){
			lastAutomaton.addFinalState(s);
		}
		//method call?
		for(Triplet<String,String, ASTRE> call : calls){
			if(v.equals(call.getValue2())){
				String name = call.getValue0() + "::" + call.getValue1();
				String path = call.getValue0().replace(".","_") + ".xal";
				String nameMetric = "method_call_to_" + call.getValue2().hashCode();
				XALMetric m = new XALMetric(nameMetric, XALProduction.ProductionType.automaton , "", name, path, new XALEnumeration());
				lastAutomaton.getActionPool().addProduction(m);
				s.setNameMetric(nameMetric);
			}
		}
	}

	private void getXAL(SyncNode sv) {
		XALAutomaton bck = lastAutomaton;
		XALAddState bckAut = aut;
		XALAutomaton automa = new XALAutomaton("sync_on_" + sv.getExpr() + "_" + sv.getID());
		automa.setGlobalState(globalVars);
		doc.addAutomaton(automa);
		//creating metrics
		String name = automa.getId();
		String path = doc.getFilename();

		for(Node from : sv.getNodesEntered()){
			String nameMetric = "to_sync_node_" + from.getID();
			XALMetric m = new XALMetric(nameMetric, XALProduction.ProductionType.automaton , "", name, path, new XALEnumeration());

			lastAutomaton.getActionPool().addProduction(m);
			doc.getNodeFromNumericID(from.getID()).setNameMetric(nameMetric);
			doc.getNodeFromNumericID(from.getID()).setSynchronized(true);
		}

		lastAutomaton = automa;
		aut = automa;
		for(Node v : sv.getNodes()){
			getXAL(v);
		}
		for(Edge e : sv.getNewEdges()){
			getXAL(e);
		}
		lastAutomaton = bck;
		aut = bckAut;
	}

	private void getXAL(IEdge e) {
		if(e instanceof Edge){
			getXAL((Edge)e);
		} else if(e instanceof AnonymEdge){
			getXAL((AnonymEdge)e);
		} else {
			getXAL((SyncEdge)e);
		}
	}

	private void getXAL(Edge e) {
		Node from = e.getFrom();
		Node to = e.getTo();
		XALState f = doc.getNodeFromNumericID(from.getID());
		XALState t = doc.getNodeFromNumericID(to.getID());
		if(f == null || t == null) return;
		XALTransition tt;
		if(e.getConstraint() != null && !e.getConstraint().isCategory(UndefiniteTimeout.class))
			tt  = new XALTransition(f,t, e.getLabel(), e.getConstraint().getValue());
		else
			tt = new XALTransition(f,t, e.getLabel());
		if(e.getFrom().isResetClock() && e.getTo().getConstraint() != null){
			for(Pair<String,String> r : e.getFrom().getResetVars()){
				tt.addClockReset(r.getValue0());
			}
		}
		lastAutomaton.addTransition(tt);
	}

	private void getXAL(AnonymEdge e) {
		XALState f = doc.getNodeFromNumericID(e.getFrom().getID());
		String className = e.getTo().getName();
		String _id = "definition_of_" + className;
		f.setNameMetric(_id);
		XALMetric action = new XALMetric(_id, XALProduction.ProductionType.automaton, className, doc.getFilename(), new XALEnumeration() );
		lastAutomaton.getActionPool().addProduction(action);
	}

	private void getXAL(SyncEdge e) {
		if(e.getType() == SyncEdge.TYPE.SYNC_BLOCK){
			SyncNode from = (SyncNode) e.getFrom();
			SyncNode to = (SyncNode) e.getTo();
			XALState f = doc.getSyncNodeFromNumericID(from.getID());
			XALState t = doc.getSyncNodeFromNumericID(to.getID());
			lastAutomaton.addTransition(new XALTransition(f,t));
		} else {
			Node from = (Node) e.getFrom();
			Node to = (Node) e.getTo();
			XALState f = doc.getNodeFromNumericID(from.getID());
			XALState t = doc.getNodeFromNumericID(to.getID());
			lastAutomaton.addTransition(new XALTransition(f,t));
		}
	}
}
