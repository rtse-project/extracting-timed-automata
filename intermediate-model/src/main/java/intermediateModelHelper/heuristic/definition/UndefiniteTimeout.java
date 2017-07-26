package intermediateModelHelper.heuristic.definition;

import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeUndefinedTimeout;

import java.util.List;


/**
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class UndefiniteTimeout extends SearchTimeConstraint {

	List<TimeUndefinedTimeout>  timeMethods = TemporalInfo.getInstance().getTimeUndefinedTimeout();


	public UndefiniteTimeout() {
	}

	@Override
	public void setup(ASTClass c) {
		super.setup(c);
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
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				String pointer = elm.getClassPointed();
				String name = elm.getMethodName();
				List<IASTRE> pars = elm.getParameters();
				int size = pars.size();
				if(pointer != null && containTimeOut(pointer, name, size)) {
					String timeout = "";
					UndefiniteTimeout.super.addConstraint(timeout, elm, true);
				}
			}
		});
	}

	private boolean containTimeOut(String pointer, String name, int nPars){
		for(TimeUndefinedTimeout m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}


}
