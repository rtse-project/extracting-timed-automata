package intermediateModel.interfaces;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public enum TimeUnit {

	UNKNOWN ("Unknown"),
	;

	private final String text;

	/**
	 * @param text
	 */
	TimeUnit(final String text) {
		this.text = text;
	}

	/* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
	@Override
	public String toString() {
		return text;
	}

}
