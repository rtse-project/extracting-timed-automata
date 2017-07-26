package intermediateModelHelper.envirorment;


/**
 * The class handle kinda of a symbol table for dynamic scoping.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class EnvBase extends Env {

	public EnvBase() {
	}

	public EnvBase(Env prev) {
		super(prev);
	}
}
