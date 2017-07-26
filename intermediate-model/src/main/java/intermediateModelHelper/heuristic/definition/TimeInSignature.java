package intermediateModelHelper.heuristic.definition;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTConstructor;
import intermediateModel.structure.ASTMethod;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.structure.expression.ASTNewObject;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.envirorment.temporal.structure.RuntimeConstraint;
import intermediateModelHelper.envirorment.temporal.structure.TimeMethod;

import java.util.List;
import java.util.Stack;


/**
 *
 * TODO: Define how process user annotations.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TimeInSignature extends SearchTimeConstraint {

	List<TimeMethod>  timeMethods = TemporalInfo.getInstance().getMethodsWithTimeInSignature();

	IASTMethod currentMethod;
	String className;

	public TimeInSignature() {
	}

	@Override
	public void setup(ASTClass c) {
		super.setup(c);
		className = c.getPackageName() + "." + c.getName();
	}

	/**
	 * The search accept only {@link ASTRE}, in particular it checks only {@link ASTMethodCall}. <br>
	 * The heuristc search if the method called is in the list of time relevant methods.
	 * Moreover, in order to be a time method, the calee expression must be in the envirorment as well.
	 *
	 * @param stm	Statement to process
	 * @param env	Envirorment visible to that statement
	 */
	@Override
	public void next(ASTRE stm, Env env) {
		//works only on ASTRE
		IASTRE expr = stm.getExpression();
		if(expr == null){
			return;
		}
		expr.visit(new DefualtASTREVisitor(){

			Stack<Boolean> check = new Stack<>();
			{
				check.push(true);
			}

			@Override
			public void enterASTNewObject(ASTNewObject elm) {
				if(elm.getHiddenClass() != null){
					check.push(false);
				}
			}

			@Override
			public void exitASTNewObject(ASTNewObject elm) {
				if(elm.getHiddenClass() != null){
					check.pop();
				}
			}

			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				if(!check.peek()) return;

				String pointer = elm.getClassPointed();
				String name = elm.getMethodName();
				List<IASTRE> pars = elm.getParameters();
				int size = pars.size();
				if(pointer != null && containTimeOut(pointer, name, size)) {
					elm.setTimeCall(true);
					String timeout = "";
					TimeMethod m = getTimeOut(pointer,name, size);
					int[] p = m.getTimeouts();
					for(int i : p){
						timeout += pars.get(i).getCode() + "+";
						IASTVar v = env.getVar(pars.get(i).getCode());
						if(v!=null){
							v.setTimeCritical(true);
						}
					}
					if(timeout.length() > 1)
						timeout = timeout.substring(0, timeout.length() - 1);
					Constraint c = TimeInSignature.super.addConstraint(timeout, stm, false);
					c.addRuntimeConstraints(new RuntimeConstraint(className, currentMethod.getName(), elm.getLine(), timeout));
					TimeInSignature.super.addTimeVar(currentMethod, timeout);
				}
			}
		});
	}

	private boolean containTimeOut(String pointer, String name, int nPars){
		for(TimeMethod m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}

	private TimeMethod getTimeOut(String pointer, String name, int nPars){
		for(TimeMethod m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return m;
		}
		return null;
	}

	@Override
	public void nextMethod(ASTMethod method, Env env) {
		super.nextMethod(method, env);
		currentMethod = method;
	}

	@Override
	public void nextConstructor(ASTConstructor method, Env env) {
		super.nextConstructor(method, env);
		currentMethod = method;
	}

}
