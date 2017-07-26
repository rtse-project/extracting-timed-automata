package intermediateModel.interfaces;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface IASTVar {
	String getType();
	String getTypeNoArray();
	String getName();
	boolean isTimeCritical();
	void setTimeCritical(boolean timeCritical);
	boolean equals(Object o);
	public String getTypePointed();
}
