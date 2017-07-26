package intermediateModelHelper.indexing.structure;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.sql.Timestamp;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

@Entity("DBStatus")
@Indexes({
		@Index(fields = @Field(value = "createdAt", type = IndexType.DESC)),
		@Index(fields = @Field(value = "lastEdit", type = IndexType.DESC)),
		@Index(fields = @Field(value = "dbName", type = IndexType.TEXT))
})
public class DBStatus {
	@Id
	private ObjectId id;

	String dbName;
	String basePath;
	Timestamp createdAt;
	Timestamp lastEdit;
	boolean indexed = false;

	public DBStatus() {
	}

	public DBStatus(ObjectId id, String dbName, String basePath, Timestamp createdAt, Timestamp lastEdit, boolean indexed) {
		this.id = id;
		this.dbName = dbName;
		this.basePath = basePath;
		this.createdAt = createdAt;
		this.lastEdit = lastEdit;
		this.indexed = indexed;
	}

	public DBStatus(String dbName, String basePath, Timestamp createdAt, Timestamp lastEdit, boolean indexed) {
		this(dbName,createdAt,lastEdit,indexed);
		this.basePath = basePath;
	}
	public DBStatus(String dbName, Timestamp createdAt, Timestamp lastEdit, boolean indexed) {
		this.dbName = dbName;
		this.createdAt = createdAt;
		this.lastEdit = lastEdit;
		this.indexed = indexed;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getLastEdit() {
		return lastEdit;
	}

	public void setLastEdit(Timestamp lastEdit) {
		this.lastEdit = lastEdit;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
}
