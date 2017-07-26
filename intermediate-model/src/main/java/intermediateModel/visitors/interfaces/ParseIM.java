package intermediateModel.visitors.interfaces;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.*;
import intermediateModel.structure.expression.ASTNewObject;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.BuildEnvironment;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.EnvBase;
import intermediateModelHelper.envirorment.EnvParameter;

import java.util.List;

/**
 * The following class parse the Java code of the IM creating the Env structure that contains all the variables that
 * a particular instruction can see.
 * It abstract because the purpose it to allow other classes to easily go through the IM without reimplementing the Env
 * construction or the traveller of the IM.
 * The class that extends the following one has to call the {@link #analyze(List, Env)} method to start the traveling.
 * Then, it can override various hook up to do whatever it likes to.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class ParseIM {

	protected EnvBase base_env = new EnvBase();
	protected BuildEnvironment build_base_env = BuildEnvironment.getInstance();
	protected ASTClass _class;


	public ParseIM(ASTClass _class) {
		this._class = _class;
	}

	protected ParseIM() {
	}

	public void set_class(ASTClass _class) {
		this._class = _class;
	}

	/**
	 * The following method creates the basic environment for a class.
	 * It goes through the def of all stms and set if variables are time related.
	 * At the end of the execution of the method we know if an attribute is time reletad or not.
	 * @param c Class to analyze
	 */
	protected EnvBase createBaseEnv(ASTClass c){
		return createBaseEnv(c, new EnvBase());
	}

	private EnvBase createBaseEnv(ASTClass c, EnvBase e){
		base_env = (EnvBase) build_base_env.buildEnvClass(c, e);
		//check static
		for (ASTStatic s : c.getStaticInit()) {
			analyze(s.getStms(), base_env);
		}
		//is c subclass of smth?
		if(c.getParent() != null){
			base_env = createBaseEnv(c.getParent(), base_env);
		}
		return base_env;
	}

	/**
	 * For anonymous definition
	 */
	public void start(ASTClass c){
		set_class(c);
		EnvBase base = createBaseEnv(c);
		for (IASTMethod m : c.getMethods()) {
			analyze(m.getStms(), new Env(base));
		}
	}

	public void start(IASTMethod m, Env e){
		analyze(m.getStms(), e);
	}
	public void start(List<IASTStm> m, Env e){
		analyze(m, e);
	}



	protected void analyzeMethod(IASTMethod method, Env e){
		if(method instanceof ASTConstructor || method.isStatic()){
			//we have to process also the initialization of expressions
			for(ASTAttribute a : this._class.getAttributes()){
				if(a.getExpr() != null){
					analyze(a.getExpr(), e);
				}
			}
			//and the statics
			for(ASTStatic s : this._class.getStaticInit()){
				analyze(s.getStms(), e);
			}
		}
		Env eMethod = new EnvParameter(e, method.getName());
		eMethod = CheckExpression.checkPars(method.getParameters(), eMethod);
		analyze(method.getStms(), eMethod);
	}

	protected void analyzeMethod(IASTMethod method){
		analyzeMethod(method, base_env);
	}

	/**
	 * This method is a dispatcher. It is ugly, but it is the only way to implement a pattern matching.
	 * It goes through the list of {@link IASTStm} and dispatch to the method that can handle that
	 * particular type of statement.
	 * <b>Particular Note:</b> from the fact that {@link ASTTryResources} extends {@link ASTTry}, the order
	 * in the if is <u><b>REALLY IMPORTANT</b></u>. The first type must be checked before otherwise it will
	 * be dispatched always to the latter type.
	 * @param stms	List of statements to analyze
	 * @param env	Environment that is visible to that list of statements
	 */
	protected void analyze(List<IASTStm> stms, Env env){
		for(IASTStm stm : stms) {
			if (stm instanceof ASTRE){
				analyze((ASTRE) stm, env);
			}
			else if(stm	instanceof ASTBreak){
				analyze((ASTBreak)stm, env);
			}
			else if(stm	instanceof ASTContinue){
				analyze((ASTContinue)stm, env);
			}
			else if(stm	instanceof ASTDoWhile){
				analyze((ASTDoWhile)stm, env);
			}
			else if(stm	instanceof ASTFor){
				analyze((ASTFor)stm, env);
			}
			else if(stm	instanceof ASTForEach){
				analyze((ASTForEach)stm, env);
			}
			else if(stm	instanceof ASTIf){
				analyze((ASTIf)stm, env);
			}
			else if(stm	instanceof ASTReturn){
				analyze((ASTReturn)stm, env);
			}
			else if(stm	instanceof ASTSwitch){
				analyze((ASTSwitch)stm, env);
			}
			else if(stm	instanceof ASTSynchronized){
				analyze((ASTSynchronized)stm, env);
			}
			else if(stm	instanceof ASTThrow){
				analyze((ASTThrow)stm, env);
			}
			//the order is important because ASTTry is extended by ASTTryResources
			else if(stm	instanceof ASTTryResources){
				analyze((ASTTryResources)stm, env);
			}
			else if(stm	instanceof ASTTry){
				analyze((ASTTry)stm, env);
			}
			else if(stm	instanceof ASTWhile){
				analyze((ASTWhile)stm, env);
			}
			else if(stm instanceof ASTNewObject){
				analyze((ASTNewObject) stm, env);
			}
			else if(stm instanceof ASTHiddenClass){
				analyze((ASTHiddenClass)stm, env);
			}
			else {
				analyze(stm, env);
			}
		}
	}

	/**
	 * Break stm
	 * @param elm	{@link ASTBreak} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTBreak elm, Env env) {
		analyzeASTBreak(elm,env);
		analyzeEveryStm(elm,env);
	}


	/**
	 * Continue stm
	 * @param elm	{@link ASTContinue} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTContinue elm, Env env) {
		analyzeASTContinue(elm,env);
		analyzeEveryStm(elm,env);
	}


	/**
	 * new Object creation. In particular, if it has a hidden class declaration, go through it.
	 * @param stm	{@link ASTNewObject} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTNewObject stm, Env env) {
		analyzeASTNewObject(stm,env);
		analyzeEveryStm(stm,env);
		ASTHiddenClass hc = stm.getHiddenClass();
		if(hc != null){
			Env e = new Env(env);
			//e.addVar(new ASTVariable(-1,-1,"DUMMY", stm.getTypeName()));
			this.analyze(hc, e);
		}
		for(IASTRE p : stm.getParameters()){
			if(p instanceof ASTNewObject){
				analyze((ASTNewObject) p, env);
			}
		}
	}


	/**
	 * Right Expression to check.
	 * If it contains a new object, we must iterate over it as well!
	 * @param r		{@link ASTRE} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	protected void analyze(ASTRE r, Env env){
		if(r != null && r.getExpression() != null) {
			final ASTNewObject[] objs = {null};
			r.getExpression().visit(new DefaultASTVisitor() {
				@Override
				public void enterASTNewObject(ASTNewObject elm) {
					if(objs[0] == null){
						objs[0] = elm;
					}
				}
			});
			if(objs[0] != null)
				analyze(objs[0], env);
		}
		CheckExpression.checkRE(r, env);

		analyzeASTRE(r,env);
		analyzeEveryStm(r,env);
	}

	/**
	 * In the Do-While the expression is not visible by the stms and the environment and the stms environment is not
	 * visible by the expression.
	 * @param elm	{@link ASTDoWhile} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTDoWhile elm, Env env) {
		analyzeASTDoWhile(elm,env);

		this.analyze(elm.getStms(), new Env(env));
		this.analyze(elm.getExpr(), new Env(env));

		analyzeEveryStm(elm,env);
	}

	/**
	 * The for statements can see the variables in all the three right expressions.
	 *
	 * @param elm	{@link ASTFor} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTFor elm, Env env) {
		Env new_env = new Env(env);
		for(ASTRE exp : elm.getInit()) {
			this.analyze(exp, new_env);
		}
		this.analyze(elm.getExpr(), new_env);
		this.analyze(elm.getStms(), new_env);
		for(ASTRE exp : elm.getPost()){
			this.analyze(exp, new_env);
		}

		analyzeASTFor(elm,env);
		analyzeEveryStm(elm,env);
	}

	/**
	 * We extend the environment with the expression in the foreach and then check in the stms.
	 * @param elm	{@link ASTForEach} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTForEach elm, Env env) {
		Env new_env = new Env(env);
		new_env.addVar( elm.getVar() );
		this.analyze(elm.getExpr(), new_env);
		this.analyze(elm.getStms(), new_env);

		analyzeASTForEach(elm, env);
		analyzeEveryStm(elm, env);
	}

	/**
	 * The if share the guard visibility with both <b>then</b> and <b>else</b> branches.
	 * But, the <b>then</b> and <b>else</b> have a different environment.
	 * @param elm	{@link ASTIf} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTIf elm, Env env) {
		analyzeASTIf(elm, env);

		Env new_env = new Env(env);
		this.analyze(elm.getGuard(), new_env);
		this.analyze(elm.getIfBranch().getStms(), new Env(new_env));
		if(elm.getElseBranch() != null) {
			this.analyze(elm.getElseBranch().getStms(), new Env(new_env));
		}

		analyzeEveryStm(elm, env);
	}

	/**
	 * The return extends the environment with the expression (if it is present).
	 * @param elm	{@link ASTReturn} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTReturn elm, Env env) {
		if(elm.getExpr() != null)
			this.analyze(elm.getExpr(), env);

		analyzeASTReturn(elm, env);
		analyzeEveryStm(elm,env);
	}

	/**
	 * The switch shares the visibility of the expression with each case block.
	 * However, each case block has its own environment.
	 * @param elm	{@link ASTSwitch} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTSwitch elm, Env env) {
		Env new_env = new Env(env);
		this.analyze(elm.getExpr(), new_env);
		for (ASTSwitch.ASTCase c : elm.getCases()) {
			this.analyze( c.getStms(), new Env(new_env));
		}

		analyzeASTSwitch(elm,env);
		analyzeEveryStm(elm,env);
	}

	/**
	 * In a synchronized block the statements can see the expression on which we lock on the object.
	 * @param elm	{@link ASTSynchronized} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTSynchronized elm, Env env) {
		Env new_env = new Env(env);
		this.analyze(elm.getExpr(), new_env);
		this.analyze(elm.getStms(), new_env);

		analyzeASTSynchronized(elm,env);
		analyzeEveryStm(elm,env);
	}

	/**
	 * Throw statement extends the environment with the right expression that it carries.
	 * @param elm	{@link ASTThrow} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTThrow elm, Env env) {
		this.analyze(elm.getExpr(), env);

		analyzeEveryStm(elm,env);
		analyzeASTThrow(elm, env);
	}

	/**
	 * The try/catch/finally branches have a separate environment.
	 * Moreover, each catch block has its own environment.
	 * @param elm	{@link ASTTry} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTTry elm, Env env) {
		Env new_env_try = new Env(env);
		Env new_env_finally = new Env(env);

		analyze(elm.getTryBranch().getStms(), new_env_try);
		for(ASTTry.ASTCatchBranch catchBranch : elm.getCatchBranch()){
			Env new_env_catch = new Env(env);
			new_env_catch.addVar(catchBranch.getExpr());
			analyze( catchBranch.getStms(), new_env_catch );
		}
		if(elm.getFinallyBranch() != null)
			analyze(elm.getFinallyBranch().getStms(), new_env_finally);

		analyzeASTTry(elm,env);
		analyzeEveryStm(elm,env);
	}

	/**
	 * The try/catch/finally branches have a separate environment.
	 * Moreover, each catch block has its own environment.
	 * All the blocks share the environment with the resources.
	 * @param elm	{@link ASTTryResources} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTTryResources elm, Env env) {
		Env env_resource = new Env(env);
		for(ASTRE r : elm.getResources()){
			this.analyze(r, env_resource);
		}
		Env new_env_try = new Env(env_resource);
		Env new_env_finally = new Env(env_resource);
		analyze(elm.getTryBranch().getStms(), new_env_try);
		for(ASTTry.ASTCatchBranch catchBranch : elm.getCatchBranch()){
			Env new_env_catch = new Env(env_resource);
			new_env_catch.addVar(catchBranch.getExpr());
			analyze( catchBranch.getStms(), new_env_catch );
		}
		if(elm.getFinallyBranch() != null)
			analyze(elm.getFinallyBranch().getStms(), new_env_finally);

		analyzeASTTryResources(elm,env);
		analyzeEveryStm(elm,env);
	}

	/**
	 * The statement in the while can access to the guard.
	 * @param elm	{@link ASTWhile} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTWhile elm, Env env) {
		analyzeASTWhile(elm, env);

		Env new_env = new Env(env);
		//new_env.addVar(new ASTVariable(-1,-1,"WHILE","WHILE"));
		this.analyze(elm.getExpr(), new_env);
		this.analyze(elm.getStms(), new_env);

		analyzeEveryStm(elm,env);
	}

	/**
	 * We have a hidden class, thus we iterate over its methods with a new env.
	 * We extend the environment with the base attributes and methods of the hidden class.
	 * @param elm	{@link ASTHiddenClass} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(ASTHiddenClass elm, Env env) {
		EnvBase oldBase = base_env;
		Env new_env = this.createBaseEnv(elm, new EnvBase(env));
		//Env new_env = build_base_env.buildEnvClass(elm, env);
		//check static
		for (ASTStatic s : elm.getStaticInit()) {
			this.analyze(s.getStms(), new_env);
		}
		analyzeASTHiddenClass(elm, new_env);
		//check method
		for (IASTMethod m : elm.getMethods()) {
			Env eMethod = new EnvParameter(new_env, m.getName());
			//eMethod.addVar(new ASTVariable(-1,-1,"DUMMY_METHOD",m.getName()));
			eMethod = CheckExpression.checkPars(m.getParameters(), eMethod);
			this.analyze(m.getStms(), eMethod);
		}
		analyzeEveryStm(elm, env);
		endAnalyzeHiddenClass(elm, env);
		base_env = oldBase;
	}

	/**
	 * Fall over method. If we forgot something, we will know ;)
	 * @param r	{@link IASTStm} instruction to check.
	 * @param env	{@link Env} visible by the instruction.
	 */
	private void analyze(IASTStm r, Env env){
		System.err.println("Not Implemented Yet :: " + r.getClass().getSimpleName());
	}

	/*
	 * The following is the list of methods that other classes can override to implement their own logic
	 */

	/**
	 * Empty.
	 * @param stm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTNewObject(ASTNewObject stm, Env env) {}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTDoWhile(ASTDoWhile elm, Env env){}
	/**
	 * Empty.
	 * @param r 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTRE(ASTRE r, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTFor(ASTFor elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTForEach(ASTForEach elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTIf(ASTIf elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTReturn(ASTReturn elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTSwitch(ASTSwitch elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTSynchronized(ASTSynchronized elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTThrow(ASTThrow elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTTry(ASTTry elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTTryResources(ASTTryResources elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTWhile(ASTWhile elm, Env env){}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTBreak(ASTBreak elm, Env env){};
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTContinue(ASTContinue elm, Env env){};
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeASTHiddenClass(ASTHiddenClass elm, Env env){};
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void endAnalyzeHiddenClass(ASTHiddenClass elm, Env env) {}
	/**
	 * Empty.
	 * @param elm 	Statement
	 * @param env   Environment
	 */
	protected void analyzeEveryStm(IASTStm elm, Env env){};

}
