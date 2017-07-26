package timeannotation.definition;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ClockAnnotation implements Annotation {
	private String name;

	public ClockAnnotation() {
	}

	public ClockAnnotation(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "@Clock(name=\"" + this.name + "\")";
	}
}
