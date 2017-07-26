package intermediateModelHelper.indexing.structure;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

/**
 * The following class is used to save some data in a MongoDB.
 * The data stored for a class consists in:
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

@Entity("IndexSocket")
@Indexes(
		@Index(fields = @Field(value = "$**", type = IndexType.TEXT))
)
public class IndexSocket {
	@Id
	private ObjectId id;
	private int line = -1;
	private String filename = "";
	private String name = "";
	private String methodName = "";
	private String fullName = "";

	public IndexSocket() {
	}

	public IndexSocket(int line, String filename, String className, String methodName) {
		this.line = line;
		this.filename = filename;
		this.name = className;
		this.methodName = methodName;
		this.fullName = className + "$" + methodName + ":" + line;
	}

	public IndexSocket(ObjectId id, int line, String filename, String className, String methodName, String fullName) {
		this.id = id;
		this.line = line;
		this.filename = filename;
		this.name = className;
		this.methodName = methodName;
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IndexSocket that = (IndexSocket) o;

		if (line != that.line) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
		return fullName != null ? fullName.equals(that.fullName) : that.fullName == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + line;
		result = 31 * result + (filename != null ? filename.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
		result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
		return result;
	}
}
