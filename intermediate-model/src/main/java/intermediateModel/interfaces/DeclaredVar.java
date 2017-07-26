package intermediateModel.interfaces;

/**
 * Created by giovanni on 15/05/2017.
 */
public class DeclaredVar {
    protected String type;
    protected String name;
    protected String id;

    public DeclaredVar() {
    }

    public DeclaredVar(String type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", type, name, id);
    }
}