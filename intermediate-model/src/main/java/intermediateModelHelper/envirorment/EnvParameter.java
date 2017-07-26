package intermediateModelHelper.envirorment;


/**
 * The class handle kinda of a symbol table for dynamic scoping.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class EnvParameter extends Env {

	String methodName;

	public EnvParameter(Env prev, String methodName) {
		super(prev);
		this.methodName = methodName;
	}

	/**
	 * Create the Object on the top of the previous Env
	 * @param prev	Previous environment
	 */
	public EnvParameter(Env prev) {
		super(prev);
	}

	public String getMethodName() {
		return methodName;
	}
}
