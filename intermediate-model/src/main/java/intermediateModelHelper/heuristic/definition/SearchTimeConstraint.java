package intermediateModelHelper.heuristic.definition;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.*;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * The Class should be inherit in order to specify new Strategies to find time constraint.
 * It defines a list of where there are time constraint found.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 */
public abstract class SearchTimeConstraint {

	/**
	 * Stores the list of {@link Triplet} of time instruction the server.heuristic found.
	 * It saves:
	 * <ul>
	 *     <li>Message</li>
	 *     <li>Node of IM</li>
	 *     <li>Type of Heuristic that found the constraint</li>
	 * </ul>
	 */
	protected List<Constraint> timeConstraint = new ArrayList<>();
	protected HashMap<IASTMethod, List<String>> timeVars = new HashMap<>();
	protected ASTClass c = null;
	protected String methodName = "";
	/**
	 * It used to set up internal resources
	 */
	public void setup(ASTClass c){
		this.c = c;
	}

	/**
	 * It used to accept a Statement
	 * @param stm	Statement to process
	 * @param env	Envirorment visible to that statement
	 */
	public abstract void next(ASTRE stm, Env env);


	public void nextIfExpr(ASTRE stm, Env env){
		next(stm,env);
	}

	public void nextWhileExpr(ASTRE stm, Env env, ASTWhile w){
		next(stm,env);
	}

	/**
	 * It used to accept a new Method Definition
	 * @param method	Statement to process
	 * @param env	Envirorment visible to that statement
	 */
	public void nextMethod(ASTMethod method, Env env){
		setMethodName(method);
	}

	/**
	 * It used to accept a new Constructor Definition
	 * @param method	Statement to process
	 * @param env	Envirorment visible to that statement
	 */
	public void nextConstructor(ASTConstructor method, Env env){
		setMethodName(method);
	}

	private void setMethodName(IASTMethod name){
		methodName = name.getName();
	}

	/**
	 * Add a constraint to the list
	 * @param message Message to store with the time constraint
	 * @param stm	The instruction to add to the list
	 */
	protected Constraint addConstraint(String message, IASTStm stm, boolean calculateRuntime){
		int line = stm.getLine();
		Constraint elm = new Constraint(stm, getClass(), message, line, c, methodName, calculateRuntime);
		if(!timeConstraint.contains(elm))
			timeConstraint.add( elm );
		stm.addConstraint( elm );
		//add the log of the constraint

		return elm;
	}

	protected void addTimeVar(IASTMethod method, String name){
		if(timeVars.containsKey(method)){
			List<String> l = timeVars.get(method);
			if(!l.contains(name))
				l.add(name);
		} else {
			List<String> l = new ArrayList<>();
			l.add(name);
			timeVars.put(method,l);
		}
	}

	/**
	 * Getter.
	 * @return The list of constraint.
	 */
	public List<Constraint> getTimeConstraint() {
		return timeConstraint;
	}

	public HashMap<IASTMethod, List<String>> getTimeVars() {
		return timeVars;
	}
}
