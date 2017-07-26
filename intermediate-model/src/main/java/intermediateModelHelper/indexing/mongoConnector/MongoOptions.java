package intermediateModelHelper.indexing.mongoConnector;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class MongoOptions {
	private String ip = "localhost";
	private final int port = 27017;
	private String dbName = "_test_";
	private final static MongoOptions instance = new MongoOptions();

	protected MongoOptions() {
	}

	public static synchronized MongoOptions getInstance(){
		return instance;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
