package intermediateModel.visitors;


import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.*;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.BuildEnvironment;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.heuristic.definition.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 * This class is the manager that apply all the heuristics we created.
 * It goes through all {@link IASTMethod} definition of a {@link ASTClass}.
 * For each statement it extends the {@link Env} and apply all the subscribed strategies.
 */
public class ApplyHeuristics extends ParseIM {

	private boolean __DEBUG__ = false;

	protected List<SearchTimeConstraint> strategies = new ArrayList<>();
	protected List<Class<? extends SearchTimeConstraint>> strategiesTypes = new ArrayList<>();

	public ApplyHeuristics(){
		build_base_env = BuildEnvironment.getInstance();
	}
	public ApplyHeuristics(BuildEnvironment env) {
		this.build_base_env = env;
	}

	/**
	 * The following method simplify the usage of the current class.
	 * It gets, with a set of predefined heuristics, the time constraints of the given class.
	 * In particular, the list of heuristics that it uses are:
	 * <ul>
	 *     <li>{@link SetTimeout}</li>
	 *     <li>{@link TimeoutResources}</li>
	 *     <li>{@link TimeInSignature}</li>
	 * </ul>
	 * @param c	Class to analyze
	 * @return	List of time constraints with the predefined set of heuristics
	 */
	public static List<Constraint> getConstraint(ASTClass c){
		//return new ArrayList<>();

		ApplyHeuristics ah = new ApplyHeuristics();
		ah.set__DEBUG__(false);
		//ah.subscribe(ThreadTime.class);
		//ah.subscribe(SocketTimeout.class);
		ah.subscribe(TimeoutResources.class);
		ah.subscribe(UndefiniteTimeout.class);
		//ah.subscribe(TimerType.class);
		ah.subscribe(TimeInSignature.class);
		ah.subscribe(SetTimeout.class);
		ah.subscribe(AssignmentTimeVar.class);
		ah.analyze(c);
		return ah.getTimeConstraint();
	}

	public void set__DEBUG__(boolean __DEBUG__) {
		this.__DEBUG__ = __DEBUG__;
	}

	public void subscribe(Class<? extends SearchTimeConstraint> strategy){
		strategiesTypes.add(strategy);
	}
	public void subscribe(SearchTimeConstraint objInstance) {
		strategies.add(objInstance);
	}

	/**
	 * This method trigger the application of the server.heuristic and analysis of the code.
	 * @param c Class to analyze.
	 */
	public void analyze(ASTClass c){
		//instantiate the strategies
		strategies.clear();
		for(Class<? extends SearchTimeConstraint> type : strategiesTypes){
			SearchTimeConstraint a = null;
			try {
				a = type.newInstance();
				a.setup(c);
				strategies.add(a);
			} catch (InstantiationException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		super.set_class(c);
		Env base = super.createBaseEnv(c);
		ExtractTimeAttribute timeAttribute = new ExtractTimeAttribute(c);
		for(IASTVar p : timeAttribute.getTimeAttributes()){
			if(base.existVarName(p.getName())){
				IASTVar v = base.getVar(p.getName());
				v.setTimeCritical(true);
			}
		}
		if(__DEBUG__){
			System.out.println("List of TIMED ATTRIBUTEs : " + timeAttribute.getTimeAttributes().size());
			for(IASTVar p : timeAttribute.getTimeAttributes()){
				System.out.format("\t %s %s\n", p.getName(), p.getType());
			}
		}
		//first constructor
		for(IASTMethod m : c.getMethods()){
			if(m instanceof ASTMethod) continue;
			Env eMethod = new Env(base);
			eMethod = CheckExpression.checkPars(m.getParameters(), eMethod);
			analyzeMethod(m, eMethod);
			analyze(m.getStms(), eMethod );
		}

		//then methods
		for(IASTMethod m : c.getMethods()){
			if(m instanceof ASTConstructor) continue;
			Env eMethod = new Env(base);
			eMethod = CheckExpression.checkPars(m.getParameters(), eMethod);
			analyzeMethod(m, eMethod);
			analyze(m.getStms(), eMethod );
		}

		//storing vars in methodss
		for(IASTMethod m : c.getMethods()) {
			List<String> vars = getMethodTimeVars(m);
			m.setTimeVars(vars);
		}
	}

	@Override
	protected void analyzeMethod(IASTMethod method, Env e) {
		//mark env with time
		MarkTime mk = new MarkTime();
		ParseIM parser = new ParseIM() {
			@Override
			protected void analyze(ASTRE r, Env env) {
				mk.next(r, env);
			}
		};
		parser.start(method, new Env(e));
		if(method instanceof ASTConstructor) {
			for(SearchTimeConstraint s : strategies){
				s.nextConstructor((ASTConstructor) method,e);
			}
		} else {
			for(SearchTimeConstraint s : strategies){
				s.nextMethod((ASTMethod) method,e);
			}
		}
	}

	@Override
	protected void analyzeEveryStm(IASTStm elm, Env env) {

	}

	@Override
	protected void analyzeASTDoWhile(ASTDoWhile elm, Env env) {
		super.analyze(elm.getStms(), env);
		//super.analyze(elm.getStms(), env);
		applyStepWhileExpr(elm.getExpr(), env, elm);
	}

	@Override
	protected void analyzeASTWhile(ASTWhile elm, Env env) {
		super.analyzeASTWhile(elm, env);
		applyStepWhileExpr(elm.getExpr(), env, elm);
	}

	@Override
	protected void analyzeASTIf(ASTIf elm, Env env) {
		super.analyzeASTIf(elm, env);
		applyStepIFExpr(elm.getGuard(), env);
	}

	@Override
	protected void analyzeASTRE(ASTRE r, Env env) {
		applyStep(r, env);
	}

	private void applyStep(ASTRE stm, Env env){
		if(stm == null) return;
		for(SearchTimeConstraint s : strategies){
			s.next(stm, env);
		}
	}
	private void applyStepIFExpr(ASTRE stm, Env env){
		if(stm == null) return;
		for(SearchTimeConstraint s : strategies){
			s.nextIfExpr(stm, env);
		}
	}
	private void applyStepWhileExpr(ASTRE stm, Env env, ASTWhile w){
		if(stm == null) return;
		for(SearchTimeConstraint s : strategies){
			s.nextWhileExpr(stm, env, w);
		}
	}

	public List<Constraint> getTimeConstraint() {
		List<Constraint> out = new ArrayList<>();
		for(SearchTimeConstraint s : strategies){
			out.addAll(s.getTimeConstraint());
		}
		return out;
	}

	public List<String> getMethodTimeVars(IASTMethod m) {
		List<String> out = new ArrayList<>();
		for(SearchTimeConstraint s : strategies){
			HashMap<IASTMethod,List<String>> tmp = s.getTimeVars();
			if(tmp.containsKey(m)){
				out.addAll(tmp.get(m));
			}
		}
		return out;
	}


}
