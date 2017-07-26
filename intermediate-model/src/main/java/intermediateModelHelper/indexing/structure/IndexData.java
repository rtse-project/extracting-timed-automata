package intermediateModelHelper.indexing.structure;

import com.google.common.annotations.Beta;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.ArrayList;
import java.util.List;

/**
 * The following class is used to save some data in a MongoDB.
 * The data stored for a class consists in:
 * <ul>
 *     <li>List of methods {@link IndexMethod}</li>
 *     <li>List of synchronized methods {@link IndexMethod}</li>
 *     <!--<li>List of synchronized blocks {@link IndexSyncBlock}</li>-->
 *     <li><b>BETA</b> List of timed related methods</li>
 *     <li>Package of the class</li>
 *     <li>Class Name</li>
 *     <li>Extended Class</li>
 *     <li>List of implemented interfaces</li>
 * </ul>
 * We ensure that the object stored in MongoDB has two indexes on (i) Class Name and (ii) Package Name of each class.
 * The class is just a <i>Struct</i> that serves the purpose of storing information only.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

@Entity("IndexData")
@Indexes(
		@Index(fields = @Field(value = "$**", type = IndexType.TEXT))
)
public class IndexData {

	public static class IndexTimeMethod {
		String name;
		List<String> signature;

		public IndexTimeMethod(String name, List<String> signature) {
			this.name = name;
			this.signature = signature;
		}

		public IndexTimeMethod() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getSignature() {
			return signature;
		}

		public void setSignature(List<String> signature) {
			this.signature = signature;
		}
	}

	@Id
	private ObjectId id;
	List<IndexMethod> listOfMethods = new ArrayList<>();
	@Beta
	List<IndexTimeMethod> listOfTimedMethods = new ArrayList<>();
	List<IndexMethod> listOfSyncMethods = new ArrayList<>();
	List<IndexParameter> timeAttribute = new ArrayList<>();
	List<String> imports = new ArrayList<>();

	String classPackage = "";
	String fullclassPackage = "";
	String name = "";
	String extendedType = "";
	String fullName = "";
	String path = "";
	List<String> interfacesImplemented = new ArrayList<>();
	boolean isInterface;
	boolean isAbstract;

	public IndexData() {
	}

	public IndexData(ObjectId id, List<IndexMethod> listOfMethods, List<IndexTimeMethod> listOfTimedMethods, List<IndexMethod> listOfSyncMethods, List<IndexParameter> timeAttribute, List<String> imports, String classPackage, String fullclassPackage, String name, String extendedType, String fullName, String path, List<String> interfacesImplemented, boolean isInterface, boolean isAbstract) {
		this.id = id;
		this.listOfMethods = listOfMethods;
		this.listOfTimedMethods = listOfTimedMethods;
		this.listOfSyncMethods = listOfSyncMethods;
		this.timeAttribute = timeAttribute;
		this.imports = imports;
		this.classPackage = classPackage;
		this.fullclassPackage = fullclassPackage;
		this.name = name;
		this.extendedType = extendedType;
		this.fullName = fullName;
		this.path = path;
		this.interfacesImplemented = interfacesImplemented;
		this.isInterface = isInterface;
		this.isAbstract = isAbstract;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name;
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

	public List<IndexMethod> getListOfMethods() {
		return listOfMethods;
	}

	public void setListOfMethods(List<IndexMethod> listOfMethods) {
		this.listOfMethods = listOfMethods;
	}

	public List<IndexTimeMethod> getListOfTimedMethods() {
		return listOfTimedMethods;
	}

	public void setListOfTimedMethods(List<IndexTimeMethod> listOfTimedMethods) {
		this.listOfTimedMethods = listOfTimedMethods;
	}

	public List<IndexMethod> getListOfSyncMethods() {
		return listOfSyncMethods;
	}

	public void setListOfSyncMethods(List<IndexMethod> listOfSyncMethods) {
		this.listOfSyncMethods = listOfSyncMethods;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean anInterface) {
		isInterface = anInterface;
	}

	public String getClassPackage() {
		return classPackage;
	}

	public void setClassPackage(String classPackage) {
		this.classPackage = classPackage;
	}

	public String getClassName() {
		return name;
	}

	public void setClassName(String className) {
		this.name = className;
	}

	public String getExtendedType() {
		return extendedType;
	}

	public void setExtendedType(String extendedType) {
		this.extendedType = extendedType;
	}

	public List<String> getInterfacesImplemented() {
		return interfacesImplemented;
	}

	public void setInterfacesImplemented(List<String> interfacesImplemented) {
		this.interfacesImplemented = interfacesImplemented;
	}

	public void addMethod(IndexMethod m){
		this.listOfMethods.add(m);
	}

	public void addSyncMethod(IndexMethod m) {
		this.listOfSyncMethods.add(m);
	}

	public List<IndexParameter> getTimeAttribute() {
		return timeAttribute;
	}

	public void setTimeAttribute(List<IndexParameter> timeAttribute) {
		this.timeAttribute = timeAttribute;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFullclassPackage() {
		return fullclassPackage;
	}

	public void setFullclassPackage(String fullclassPackage) {
		this.fullclassPackage = fullclassPackage;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean anAbstract) {
		isAbstract = anAbstract;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IndexData)) return false;

		IndexData data = (IndexData) o;

		if (isInterface() != data.isInterface()) return false;
		if (getId() != null ? !getId().equals(data.getId()) : data.getId() != null) return false;
		if (getListOfMethods() != null ? !getListOfMethods().equals(data.getListOfMethods()) : data.getListOfMethods() != null)
			return false;
		if (getListOfTimedMethods() != null ? !getListOfTimedMethods().equals(data.getListOfTimedMethods()) : data.getListOfTimedMethods() != null)
			return false;
		if (getListOfSyncMethods() != null ? !getListOfSyncMethods().equals(data.getListOfSyncMethods()) : data.getListOfSyncMethods() != null)
			return false;
		if (getTimeAttribute() != null ? !getTimeAttribute().equals(data.getTimeAttribute()) : data.getTimeAttribute() != null)
			return false;
		if (getImports() != null ? !getImports().equals(data.getImports()) : data.getImports() != null) return false;
		if (getClassPackage() != null ? !getClassPackage().equals(data.getClassPackage()) : data.getClassPackage() != null)
			return false;
		if (getName() != null ? !getName().equals(data.getName()) : data.getName() != null) return false;
		if (getExtendedType() != null ? !getExtendedType().equals(data.getExtendedType()) : data.getExtendedType() != null)
			return false;
		if (getFullName() != null ? !getFullName().equals(data.getFullName()) : data.getFullName() != null)
			return false;
		if (getPath() != null ? !getPath().equals(data.getPath()) : data.getPath() != null) return false;
		return getInterfacesImplemented() != null ? getInterfacesImplemented().equals(data.getInterfacesImplemented()) : data.getInterfacesImplemented() == null;

	}


	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getListOfMethods() != null ? getListOfMethods().hashCode() : 0);
		result = 31 * result + (getListOfTimedMethods() != null ? getListOfTimedMethods().hashCode() : 0);
		result = 31 * result + (getListOfSyncMethods() != null ? getListOfSyncMethods().hashCode() : 0);
		result = 31 * result + (getTimeAttribute() != null ? getTimeAttribute().hashCode() : 0);
		result = 31 * result + (getImports() != null ? getImports().hashCode() : 0);
		result = 31 * result + (getClassPackage() != null ? getClassPackage().hashCode() : 0);
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		result = 31 * result + (getExtendedType() != null ? getExtendedType().hashCode() : 0);
		result = 31 * result + (getFullName() != null ? getFullName().hashCode() : 0);
		result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
		result = 31 * result + (getInterfacesImplemented() != null ? getInterfacesImplemented().hashCode() : 0);
		result = 31 * result + (isInterface() ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "IndexData{" +
				"classPackage='" + classPackage + '\'' +
				", className='" + name + '\'' +
				"}\n";
	}
}
