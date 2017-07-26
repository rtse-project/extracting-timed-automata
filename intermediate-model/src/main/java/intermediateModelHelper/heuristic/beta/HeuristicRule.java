package intermediateModelHelper.heuristic.beta;

/**
 * Abstract class that gives the structure of how an heuristic
 * must be in order to be part of our rewriting system.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class HeuristicRule {
	/**
	 * Method used to check if the current heuristic has at least one match in the IM.
	 * @return true if there is at least one instance that the heuristic can work on.
	 */
	//abstract boolean match();
	abstract boolean where();

	/**
	 * Method used to allow the heuristic to create new annotations to the nodes.
	 * is it really necessary?
	 */
	abstract void let();

	/**
	 * Method that is used to rewrite some parts of the nodes in the IM with annotations.
	 */
	abstract void rewrite();

	/**
	 * Optional Method to overwritten.
	 * If it not needed by the heuristic, just do nothing.
	 * I am not feel confident with it. What are the possible side condition that a method
	 * can use? why not put all the code inside the rewrite?
	 */
	protected void sideCondition(){ }
}
