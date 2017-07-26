package intermediateModelHelper.heuristic.definition;

import intermediateModel.structure.ASTRE;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.Env;

/**
 * The {@link MarkTime} searches for instances of time assignment
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 */
public class MarkTime extends SearchTimeConstraint {

	@Override
	public void next(ASTRE stm, Env env) {
		CheckExpression.checkRE(stm,env);
	}


}
