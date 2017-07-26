package intermediateModel.interfaces;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface IASTExpression {
	public enum Type {
		PLUS,
		MUL,
		MINUS,
		DIV,
		SHIFHT,
		NOT,
		METHODCALL,
		LESS,
		GREATER
	}
}
