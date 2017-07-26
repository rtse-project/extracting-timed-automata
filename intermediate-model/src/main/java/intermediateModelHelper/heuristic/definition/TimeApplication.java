package intermediateModelHelper.heuristic.definition;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTConstructor;
import intermediateModel.structure.ASTMethod;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeMethod;
import intermediateModelHelper.envirorment.temporal.structure.TimeUndefinedTimeout;

import java.util.List;

/**
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 */
public class TimeApplication extends SearchTimeConstraint {

	List<TimeUndefinedTimeout>  timeMethodsUndef = TemporalInfo.getInstance().getTimeUndefinedTimeout();
	List<TimeMethod>  timeMethods = TemporalInfo.getInstance().getMethodsWithTimeInSignature();

	IASTMethod currentMethod;
	String className;

	@Override
	public void setup(ASTClass c) {
		super.setup(c);
		className = c.getPackageName() + "." + c.getName();
	}

	@Override
	public void next(ASTRE stm, Env env) {
		//works only on ASTRE
		IASTRE expr = stm.getExpression();
		if(expr == null){
			return;
		}

		// Return Time
		if(CheckExpression.checkRE(stm,env)){
			markMethod();
		}

		//Explicit Timeout
		expr.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				String pointer = elm.getClassPointed();
				String name = elm.getMethodName();
				List<IASTRE> pars = elm.getParameters();
				int size = pars.size();
				if(pointer != null && containTimeOut(pointer, name, size)) {
					markMethod();
				}
			}
		});

		//Undefinite Timeout
		expr.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				String pointer = elm.getClassPointed();
				String name = elm.getMethodName();
				List<IASTRE> pars = elm.getParameters();
				int size = pars.size();
				if(pointer != null && containTimeOutUndef(pointer, name, size)) {
					markMethod();
				}
			}
		});

	}

	private boolean containTimeOutUndef(String pointer, String name, int nPars){
		for(TimeUndefinedTimeout m : timeMethodsUndef){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}

	protected void markMethod() {
		if(currentMethod != null)
			currentMethod.setTimeCnst(true);
	}

	private boolean containTimeOut(String pointer, String name, int nPars){
		for(TimeMethod m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
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
