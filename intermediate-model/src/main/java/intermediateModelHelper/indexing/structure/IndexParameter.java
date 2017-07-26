package intermediateModelHelper.indexing.structure;

/**
 * The following class is used to save some data in a MongoDB.
 * The data stored  consists in:
 * <ul>
 *     <li>Type of the parameter</li>
 *     <li>Name of the parameter</li>
 * </ul>
 * The class is just a <i>Struct</i> that serves the purpose of storing information only.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IndexParameter {
	String type;
	String name;

	public IndexParameter() {
	}

	public IndexParameter(String type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
