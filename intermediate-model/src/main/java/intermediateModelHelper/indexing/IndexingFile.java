package intermediateModelHelper.indexing;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.*;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.EnvBase;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexData;
import intermediateModelHelper.indexing.structure.IndexMethod;
import intermediateModelHelper.indexing.structure.IndexParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * This class created the {@link IndexData} for a given {@link ASTClass}.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IndexingFile extends ParseIM {

	String lastMethodName = "";
	List<String> signatureLastMethodName = new ArrayList<>();
	IndexData data;
	MongoConnector mongo;
	String anonymousClass = "";
	Stack<String> stackAnonymousClasses = new Stack<>();
	ASTClass _c = null;

	public IndexingFile() {
		String dbname = MongoOptions.getInstance().getDbName();
		mongo = MongoConnector.getInstance(dbname);
	}

	public IndexingFile(MongoConnector mongo) {
		this.mongo = mongo;
	}

	/**
	 * Start the indexing of a {@link ASTClass}.
	 * It force to delete the index structure from the DB and recreate it.
	 *
	 * @param c	Class to analyse.
	 * @return	The index data structure of the class.
	 */
	public IndexData index(ASTClass c){
		return index(c, false);
	}
	/**
	 * Start the indexing of a {@link ASTClass}.
	 * It goes through the methods of it and then through their statements.
	 * @param c	Class to analyze
     * @param forceReindex flag to force the recreation of the index
	 * @return	The index data structure of the class.
	 */
	public IndexData index(ASTClass c, boolean forceReindex) {
		this._c = c;
		super.set_class(c);
		if(mongo.existClassIndex(c)){
			if(forceReindex){
				mongo.delete(c);
			} else {
				List<IndexData> out = mongo.getIndex(c);
				return out.get(0);
			}
		}
		data = new IndexData();
		data.setPath(c.getPath());
		data.setClassName(c.getName());
		data.setClassPackage(c.getRealPackageName());
		data.setFullclassPackage(c.getPackageName());
		data.setImports(convertImports(c.getImports()));
		data.setInterface(c.isInterface());
		data.setAbstract(c.isAbstract());
		String fullname;
		if(c.getPackageName().trim().equals("")){
			fullname = c.getName();
		} else {
			fullname = c.getPackageName() + "." + c.getName();
		}
		data.setFullName(fullname);
		data.setExtendedType(c.getExtendClass());
		data.setInterfacesImplemented(c.getImplmentsInterfaces());
		//collecting the names of methods and sync method
		for(IASTMethod m : c.getMethods()){
			data.addMethod(prepareOutput(m));
			if(m instanceof ASTMethod){
				if(((ASTMethod) m).isSynchronized()){
					data.addSyncMethod(prepareOutput(m));
				}
			}
		}
		// add the attributes time related
		Env finalEnv = createBaseEnv(c);
		for(IASTVar v : finalEnv.getVarList()){
			if(!v.isTimeCritical()){
				continue;
			}
			IndexParameter p = new IndexParameter();
			p.setType(v.getType());
			p.setName(v.getName());
			data.getTimeAttribute().add(p);
		}
		mongo.add(data);
		return data;
	}

	private List<String> convertImports(List<ASTImport> imports) {
		List<String> out = new ArrayList<>();
		for(ASTImport i : imports){
			out.add(i.getPackagename());
		}
		return out;
	}

	/**
	 * Convert an {@link IASTMethod} Object to {@link IndexMethod} structure
	 * @param m	Method to convert
	 * @return	Its representation in the {@link IndexMethod} structure.
	 */
	private IndexMethod prepareOutput(IASTMethod m) {
		IndexMethod im = new IndexMethod();
		im.setName(m.getName());
		im.setPackageName(data.getClassPackage());
		im.setFullpackageName(data.getFullclassPackage());
		im.setFromClass(data.getClassName());
		im.setParameters(IndexMethod.convertPars(m.getParameters()));
		im.setExceptionsThrowed(m.getExceptionsThrowed());
		im.setStart(((IASTStm)m).getStart());
		im.setEnd(((IASTStm)m).getEnd());
		im.setLine(((IASTStm)m).getLine());
		im.setConstructor( m instanceof ASTConstructor );
		im.setSync( !im.isConstructor() && ((ASTMethod) m).isSynchronized() );
		im.setReturnType(m.getReturnType());
		im.setStatic(m.isStatic());
		return im;
	}

	/*
	 * Convert an {@link ASTSynchronized} Object to {@link IndexSyncBlock} structure
	 * @param m	Synchronized block to convert
	 * @param e Environment of the Synchronized block
	 * @return	Its representation in the {@link IndexMethod} structure.
	 *
	private IndexSyncBlock prepareOutput(ASTSynchronized m, Env e) {
		IndexSyncBlock is = new IndexSyncBlock();
		is.setPackageName(data.getClassPackage());
		is.setName(data.getClassName() + anonymousClass);
		is.setMethodName(lastMethodName);
		is.setExpr(m.getExpr().getCode());
		is.setStart(m.getStart());
		is.setEnd(m.getEnd());
		is.setLine(m.getLine());
		IndexEnv e_index = new IndexEnv(e);
		is.setSyncVar( e_index.getVar(is.getExpr()) );
		is.setSignature(signatureLastMethodName);
		is.setPath(data.getPath());
		return is;
	}
	*/


	/**
	 * The following method creates the basic environment for a class.
	 * It goes through the def of all stms and set if variables are time related.
	 * At the end of the execution of the method we know if an attribute is time reletad or not.
	 * <hr>
	 * <b>Efficency Tips</b>: Since we parse the IM we also collect the sync block and time related methods here.
	 * @param c Class to analyze
	 */
	@Override
	protected EnvBase createBaseEnv(ASTClass c){
		super.createBaseEnv(c);
		//check method
		for (IASTMethod m : c.getMethods()) {
			lastMethodName = m.getName();
			signatureLastMethodName = new ArrayList<>();
			for(ASTVariable p : m.getParameters()){
				signatureLastMethodName.add(p.getType());
			}
			Env eMethod = new Env(base_env);
			eMethod = CheckExpression.checkPars(m.getParameters(), eMethod);
			super.analyze(m.getStms(), eMethod);
		}
		return base_env;
	}



	@Override
	protected void analyzeASTHiddenClass(ASTHiddenClass elm, Env env) {
		stackAnonymousClasses.push(anonymousClass);
		anonymousClass += ".anonymous_class";
	}

	@Override
	protected void endAnalyzeHiddenClass(ASTHiddenClass elm, Env env) {
		anonymousClass = stackAnonymousClasses.pop();
	}

	@Override
	protected void analyzeASTRE(ASTRE r, Env env) {
		CheckExpression.checkRE(r, env);
	}

	/*@Override
	protected void analyzeASTSynchronized(ASTSynchronized elm, Env env) {
		data.addSyncBlock(prepareOutput(elm, env));
	}*/

	@Override
	protected void analyzeASTReturn(ASTReturn elm, Env env) {

		ASTRE re = elm.getExpr();
		if(re != null && re.getExpression() != null && //sanity checks
				CheckExpression.checkIt(re.getExpression(), env)){
			data.getListOfTimedMethods().add(new IndexData.IndexTimeMethod(lastMethodName, signatureLastMethodName));
		}

	}
}