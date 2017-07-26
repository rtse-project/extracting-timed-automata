package intermediateModelHelper.indexing.mongoConnector;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import intermediateModel.structure.ASTClass;
import intermediateModelHelper.indexing.structure.*;
import org.bson.BsonSerializationException;
import org.bson.Document;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Sextet;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.mongodb.morphia.query.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The following class implements a connection to MongoDB.
 * It allows to store and retrieve {@link IndexData} objects.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class MongoConnector {

	private static HashMap<String,MongoConnector> instances = new HashMap<String, MongoConnector>();

	protected MongoDatabase db;
	protected String dbName;
	protected DBStatus dbStatus;
	protected MongoClient mongoClient;
	protected MongoCollection<Document> indexCollection;
	protected final Morphia morphia = new Morphia();
	protected Datastore datastore;
	Map<String,List<IndexData>> cacheImport = new HashMap<>();
	Map<Pair<String,String>,List<IndexData>> cacheIndex = new HashMap<>();
	Map<Pair<String,String>,List<IndexData>> cacheInterface = new HashMap<>();
	Map<Pair<String,String>,List<IndexData>> cacheExtended = new HashMap<>();
	Map<Quartet<String,String, String, List<String>>,List<IndexSyncCall>> cacheSyncCall = new HashMap<>();
	Map<Pair<String,String>,List<IndexSyncCall>> cacheSyncCallClass = new HashMap<>();
	Map<Sextet<String,String,String,List<String>,Integer,Integer>,List<IndexSyncBlock>> cacheSyncBlock = new HashMap<>();
	Map<String,List<IndexSocket>> cacheSocketClass = new HashMap<>();
	Map<String,IndexSocket> cacheSocket = new HashMap<>();

	boolean isClose = false;

	String lastIp;
	int lastPort;


	/**
	 * Field enumeration
	 */
	protected final String __PACKAGE_NAME		= "classPackage";
	protected final String __CLASS_NAME 		= "name";
	protected final String __IMPORTS 			= "imports";
	protected final String __FULL_NAME 			= "fullName";
	protected final String __COLLECTION_NAME 	= "IndexData";
	protected final String __METHOD_NAME 		= "methodName";
	protected final String __IS_INTERFACE		= "isInterface";
	protected final String __EXTENDED			= "extendedType";
	protected final String __IMPLEMENTS			= "interfacesImplemented";
	protected final String __SIGNATURE 			= "signature";
	protected final String __SIGNATURE_SYNC 	= "methodSignature";
	protected final String __START	 			= "start";
	protected final String __END		 		= "end";
	protected final String __FILE				= "filename";

	/**
	 * Protected constructor. We want to give a database as singleton.
	 * @param db_name Name of the DB to use
	 */
	protected MongoConnector(String db_name, String ip, int port) {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE);
		connect(db_name,ip,port);
	}

	public void close(){
		mongoClient.close();
		isClose = true;
	}

	public boolean isClose() {
		return isClose;
	}

	public String getLastIp() {
		return lastIp;
	}

	public int getLastPort() {
		return lastPort;
	}

	public void connect(String db_name, String ip, int port){
		mongoClient = new MongoClient(ip, port);
		lastIp = ip;
		lastPort = port;
		db = mongoClient.getDatabase(db_name);
		dbName = db_name;
		indexCollection = db.getCollection(__COLLECTION_NAME);
		datastore = morphia.createDatastore(mongoClient, db_name);
		MapperOptions options = new MapperOptions();
		options.setStoreEmpties(true);
		options.setStoreNulls(true);
		morphia.getMapper().setOptions(options);
		morphia.mapPackage("intermediateModelHelper.indexing.structure");
		datastore.ensureIndexes();
		datastore.ensureCaps();
		isClose = false;

		Query<DBStatus> q = datastore.createQuery(DBStatus.class);
		q.field("dbName").equal(dbName);
		try {
			List<DBStatus> r = q.asList();
			if (!(r.size() > 0)) {
				dbStatus = new DBStatus(dbName, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), false);
				datastore.save(dbStatus);
			} else {
				dbStatus = r.get(0);
			}
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}

	public synchronized List<String> databases(){
		List<String> out = new ArrayList<>();
		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while(dbsCursor.hasNext()) {
			out.add(dbsCursor.next());
		}
		return out;
	}

	public synchronized void setBasePath(String basePath){
		if(!basePath.endsWith("/")){
			basePath = basePath + "/";
		}
		dbStatus.setBasePath(basePath);
		datastore.save(dbStatus);
	}

	public synchronized String getBasePath(){
		return dbStatus.getBasePath();
	}

	/**
	 * Get the instance of the DB with lazy initialization.
	 * @param db_name Name of the database to use
	 * @return The singleton of the selected DB.
	 */
	public static synchronized MongoConnector getInstance(String db_name) {
		if(instances.containsKey(db_name)){
			MongoConnector i = instances.get(db_name);
			if(i.isClose())
				i.connect(db_name, i.getLastIp(), i.getLastPort());
			return i;
		}
		MongoOptions options = MongoOptions.getInstance();
		MongoConnector m = new MongoConnector(db_name, options.getIp(), options.getPort());
		instances.put(db_name, m);
		return m;
	}

	/**
	 * Get the instance of the default DB with lazy initialization.
	 * @return The singleton of the selected DB.
	 */
	public static synchronized MongoConnector getInstance() {
		MongoOptions options = MongoOptions.getInstance();
		String db_name = options.getDbName();
		if(instances.containsKey(db_name)){
			MongoConnector i = instances.get(db_name);
			if(i.isClose)
				i.connect(db_name, i.getLastIp(), i.getLastPort());
			return i;
		}
		MongoConnector m = new MongoConnector(db_name, options.getIp(), options.getPort());
		instances.put(db_name, m);
		return m;
	}

	/**
	 * Check if a class is already in the DB.
	 * @param name			Name to search
	 * @param packageName	PackageName to search
	 * @return				True if it is already in the DB.
	 */
	public synchronized boolean existClassIndex(String name, String packageName){
		return getIndex(name,packageName).size() > 0;
	}

	/**
	 * Check if a class is already in the DB.
	 * @param c	{@link ASTClass} to search
	 * @return 	True if it is already in the DB.
	 */
	public synchronized boolean existClassIndex(ASTClass c){
		return existClassIndex(c.getName(), c.getPackageName());
	}

	/**
	 * Check if a class is already in the DB.
	 * @param i	{@link IndexData} to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existClassIndex(IndexData i){
		return existClassIndex(i.getClassName(), i.getClassPackage());
	}

	/**
	 * Check if a class is already in the DB in the collection of Synchronized calls.
	 * @param c	{@link ASTClass} to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existSyncCallIndexClass(ASTClass c) {
		return getSyncCallIndexClass(c.getName(),c.getPackageName()).size() > 0;
	}
	/**
	 * Check if a class is already in the DB in the collection of Synchronized calls.
	 * @param i	{@link IndexSyncCall} to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existSyncCallIndex(IndexSyncCall i){
		return datastore.createQuery(IndexSyncCall.class)
				.field("line").equal(i.getLine())
				.field("path").equal(i.getPath())
				.field("classPackage").equal(i.getClassPackage())
				.field("name").equal(i.getClassName())
				.field("methodName").equal(i.getMethodName())
				.field("methodSignature").equal(i.getMethodSignature())
				.field("_inClassPackage").equal(i.get_inClassPackage())
				.field("_inClassName").equal(i.get_inClassName())
				.field("_inMethodName").equal(i.get_inMethodName())
				.field("_signatureInMethod").equal(i.get_signatureInMethod())
				.asList().size() > 0;
		//return false;
	}
	/**
	 * Check if a class is already in the DB in the collection of Synchronized calls.
	 * @param name			Name of the class to search
	 * @param packageName	Package of the class to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existSyncCallIndex(String name, String packageName, String methodName, List<String> signature){
		return getSyncCallIndex(name,packageName, methodName, signature).size() > 0;
	}

	/**
	 * Check if a class is already in the DB in the collection of Synchronized blocks.
	 * @param c	{@link ASTClass} to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existSyncBlockIndex(ASTClass c) {
		return getSyncBlockIndexClass(c.getName(),c.getPackageName()).size() > 0;
	}
	/**
	 * Check if a class is already in the DB in the collection of Synchronized blocks.
	 * @param i	{@link IndexSyncBlock} to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existSyncBlockIndex(IndexSyncBlock i){
		return existSyncBlockIndex(i.getName(), i.getPackageName(), i.getMethodName(), i.getSignature(), i.getStart(), i.getEnd());
	}
	/**
	 * Check if a class is already in the DB in the collection of Synchronized blocks.
	 * @param name			Name of the class to search
	 * @param packageName	Package of the class to search
	 * @return	True if it is already in the DB.
	 */
	public synchronized boolean existSyncBlockIndex(String name, String packageName, String methodName, List<String> signature, int start, int end){
		return getSyncBlockIndex(name,packageName, methodName, signature, start, end).size() > 0;
	}

	/**
	 * Insert a indexed class in the database iff. it is not already present
	 * @param indexStructureClass	Indexed structure of a class to insert
	 */
	public synchronized void add(IndexData indexStructureClass){
		if(existClassIndex(indexStructureClass)){
			return;
		}
		//System.out.println("Saving " + indexStructureClass.getClassName());
		try {
			datastore.save(indexStructureClass);
		} catch (BsonSerializationException e){
			System.err.println("File too big, cannot handle it!");
		} finally {
			Pair<String,String> p = new Pair<>(indexStructureClass.getClassName(),indexStructureClass.getClassPackage());
			cacheIndex.remove(p);
			cacheInterface.remove(p);
			String extType = indexStructureClass.getExtendedType();
			boolean f = false;
			for(String imp : indexStructureClass.getImports()){
				if(imp.endsWith(extType)){
					Pair<String,String> pExt = new Pair<>(imp, extType);
					cacheExtended.remove(pExt);
					f = true;
				}
			}
			if(!f) { //if we cannot find in the import, should be in the same pkg
				Pair<String,String> pExt = new Pair<>(indexStructureClass.getClassPackage(), extType);
				cacheExtended.remove(pExt);
			}
		}
	}

	public synchronized void add(IndexSyncCall indexSyncCall){
		if(existSyncCallIndex(indexSyncCall)){
			return;
		}
		try {
			datastore.save(indexSyncCall);
			Pair<String,String> p = new Pair<>(indexSyncCall.getClassName(),indexSyncCall.getPackageName());
			cacheSyncCallClass.remove(p);
			Quartet<String,String, String, List<String>> ps = new Quartet<>(indexSyncCall.getClassName(),indexSyncCall.getPackageName(), indexSyncCall.getMethodName(), indexSyncCall.getMethodSignature());
			cacheSyncCall.remove(ps);

		} catch (BsonSerializationException e){
			System.err.println("File too big, cannot handle it!");
		}
	}

	public synchronized void add(IndexSyncBlock indexSyncBlock) {
		if(existSyncBlockIndex(indexSyncBlock)){
			return;
		}
		try {
			datastore.save(indexSyncBlock);
			Sextet<String,String,String,List<String>,Integer,Integer> p = new Sextet<>(
					indexSyncBlock.getName(),
					indexSyncBlock.getPackageName(),
					indexSyncBlock.getMethodName(),
					indexSyncBlock.getSignature(),
					indexSyncBlock.getStart(),
					indexSyncBlock.getEnd()
			);
			cacheSyncBlock.remove(p);
		} catch (BsonSerializationException e){
			System.err.println("File too big, cannot handle it!");
		}
	}

	/**
	 * Retrieve the list of classes from the database.
	 * @param name			Name of the class to get
	 * @param packageName	PackageName of the class to get
	 * @return				List of {@link IndexData} documents
	 */
	public synchronized List<IndexData> getIndex(String name, String packageName){
		return getIndex(name, packageName, false);
	}

	/**
	 * Retrieve the list of classes from the database.
	 * @param name			Name of the class to get
	 * @param packageName	PackageName of the class to get
	 * @return				List of {@link IndexData} documents
	 */
	public synchronized List<IndexData> getIndex(String name, String packageName, boolean ignoreCache){
		Pair<String,String> p = new Pair<>(name,packageName);
		if(!ignoreCache && cacheIndex.containsKey(p)){
			return cacheIndex.get(p);
		}
		List<IndexData> out =  datastore.createQuery(IndexData.class)
				.field(__CLASS_NAME).equal(name)
				.field(__PACKAGE_NAME).equal(packageName)
				.asList();
		//if(out.size() > 0) {
		cacheIndex.put(p, out);
		//}
		return out;
	}

	/**
	 * Retrieve the list of classes from the database.
	 * @param c	{@link ASTClass} to get
	 * @return	List of {@link IndexData} documents
	 */
	public synchronized List<IndexData> getIndex(ASTClass c){
		return  getIndex(c.getName(), c.getPackageName());
	}


	public synchronized List<IndexSyncCall> getSyncCallIndexClass(ASTClass c) {
		return getSyncCallIndexClass(c.getName(), c.getPackageName());
	}

	/**
	 * Retrieve the list of Sync Call from the database of a class.
	 * @param name			Name of the class to get
	 * @param packageName	PackageName of the class to get
	 * @return				List of {@link IndexData} documents
	 */
	public synchronized List<IndexSyncCall> getSyncCallIndexClass(String name, String packageName){
		Pair<String,String> p = new Pair<>(name,packageName);
		if(cacheSyncCallClass.containsKey(p)){
			return cacheSyncCallClass.get(p);
		}
		List<IndexSyncCall> out =  datastore.createQuery(IndexSyncCall.class)
				.field(__CLASS_NAME).equal(name)
				.field(__PACKAGE_NAME).equal(packageName)
				.asList();
		if(out.size() > 0) {
			cacheSyncCallClass.put(p, out);
		}
		return out;
	}

	/**
	 * Retrieve the list of Sync Call from the database.
	 * @param name			Name of the class to get
	 * @param packageName	PackageName of the class to get
	 * @param methodName    Method name to get
	 * @param methodSignature Signature of the method to search
	 * @return				List of {@link IndexData} documents
	 */
	public synchronized List<IndexSyncCall> getSyncCallIndex(String name, String packageName, String methodName, List<String> methodSignature){
		Quartet<String,String, String, List<String>> p = new Quartet<>(name,packageName, methodName, methodSignature);
		if(cacheSyncCall.containsKey(p)){
			return cacheSyncCall.get(p);
		}
		List<IndexSyncCall> out =  datastore.createQuery(IndexSyncCall.class)
				.field(__CLASS_NAME).equal(name)
				.field(__PACKAGE_NAME).equal(packageName)
				.field(__METHOD_NAME).equal(methodName)
				.field(__SIGNATURE_SYNC).equal(methodSignature)
				.asList();
		if(out.size() > 0) {
			cacheSyncCall.put(p, out);
		}
		return out;
	}

	public synchronized List<IndexSyncBlock> getSyncBlockIndex(ASTClass c) {
		return getSyncBlockIndexClass(c.getName(), c.getPackageName());
	}

	/**
	 * Retrieve the list of sync block of classes from the database.
	 * @param name			Name of the class to get
	 * @param packageName	PackageName of the class to get
	 * @return				List of {@link IndexData} documents
	 */
	public synchronized List<IndexSyncBlock> getSyncBlockIndex(String name, String packageName, String methodName, List<String> signature, int start, int end){
		Sextet<String,String,String,List<String>,Integer,Integer> p = new Sextet<>(name,packageName, methodName, signature, start, end);
		if(cacheSyncBlock.containsKey(p)){
			return cacheSyncBlock.get(p);
		}
		List<IndexSyncBlock> out =  datastore.createQuery(IndexSyncBlock.class)
				.field(__CLASS_NAME).equal(name)
				.field(__PACKAGE_NAME).equal(packageName)
				.field(__METHOD_NAME).equal(methodName)
				.field(__SIGNATURE).equal(signature)
				.field(__START).equal(start)
				.field(__END).equal(end)
				.asList();
		if(out.size() > 0) {
			cacheSyncBlock.put(p, out);
		}
		return out;
	}

	/**
	 * Retrieve the list of sync block of classes from the database.
	 * @param name			Name of the class to get
	 * @param packageName	PackageName of the class to get
	 * @return				List of {@link IndexData} documents
	 */
	public synchronized List<IndexSyncBlock> getSyncBlockIndexClass(String name, String packageName){
		List<IndexSyncBlock> out =  datastore.createQuery(IndexSyncBlock.class)
				.field(__CLASS_NAME).equal(name)
				.field(__PACKAGE_NAME).equal(packageName)
				.asList();
		return out;
	}

	/**
	 * It queries the database to retrieve all the classes that are Threads.
	 * @return list of {@link IndexData} objects.
	 */
	public synchronized List<IndexData> getThreads(){
		Query<IndexData> q = datastore.createQuery(IndexData.class);
		q.or(
				q.criteria("extendedType").equal("Thread"),
				q.criteria("interfacesImplemented").contains("Runnable")
		);
		return q.asList();
	}

	/**
	 * It queries the database to retrieve all the classes that are Threads.
	 * @return list of {@link IndexData} objects.
	 */
	public synchronized List<IndexData> getMains(){
		Query<IndexData> q = datastore.find(IndexData.class)
				.filter("listOfMethods.isStatic = ", true)
				.filter("listOfMethods.returnType = ", "void")
				.filter("listOfMethods.name = ", "main");
		return q.asList();

	}

	/**
	 * It queries the database to retrieve all the classes that contains timed methods.
	 * @return list of {@link IndexData} objects.
	 */
	public synchronized List<IndexData> getTimedMethod(){
		Query<IndexData> q = datastore.find(IndexData.class)
				.disableValidation()
				.field("listOfTimedMethods.0").exists();
		return q.asList();
	}



	/**
	 * Get the list of classes that belong to an import statement
	 * @param query	package name of the import
	 * @return	List of {@link IndexData} classes
	 */
	public synchronized List<IndexData> getFromImport(String query){
		if(!query.endsWith("*") && cacheImport.containsKey(query)){
			return cacheImport.get(query);
		}
		Query<IndexData> q = datastore.createQuery(IndexData.class);//.filter("classPackage",regexp);
		if(query.endsWith("*")){
			q.field(__FULL_NAME).contains(query);
		} else {
			q.field(__FULL_NAME).equal(query);
		}
		List<IndexData> out = q.asList();
		if(out.size() > 0) {
			cacheImport.put(query, out);
		} else { // if(forceCache){
			cacheImport.put(query, new ArrayList<>());
		}
		return out;
	}


	public synchronized List<IndexData> getClassesThatImports(String _package, String name){
		Query<IndexData> q = datastore.createQuery(IndexData.class);
		q.or(
			q.criteria(__IMPORTS).equal(_package + ".*"),
			q.criteria(__IMPORTS).equal(_package + "." + name)
		);
		return q.asList();
	}

	public synchronized void ensureIndexes(){
		datastore.ensureIndexes();
	}

	/**
	 * Getter of the DB structure.
	 * @return the database structure.
	 */
	public synchronized MongoDatabase getDb() {
		return db;
	}

	/**
	 * Getter of the DB structure.
	 * @return the database structure.
	 */
	public synchronized Datastore getDatastore() {
		return datastore;
	}

	/**
	 * Delete a class from the DB
	 * @param c	Class to remove
	 */
	public synchronized void delete(ASTClass c) {
		Query<IndexData> q = datastore.createQuery(IndexData.class)
				.field(__CLASS_NAME).equal(c.getName())
				.field(__PACKAGE_NAME).equal(c.getPackageName());
		datastore.delete(q);
		Pair<String,String> p = new Pair<>(c.getName(),c.getPackageName());
		cacheIndex.remove(p);
	}

	public synchronized List<IndexData> resolveClassImplementingInterface(String interfaceName, String packageName){
		Pair<String,String> p = new Pair<>(interfaceName, packageName);
		if(cacheInterface.containsKey(p)){
			return cacheInterface.get(p);
		}
		List<IndexData> output = new ArrayList<>();
		Query<IndexData> q;
		List<IndexData> tmp;
		if(cacheIndex.containsKey(p)){
			tmp = cacheIndex.get(p);
		} else {
			q = datastore.createQuery(IndexData.class)
					.field(__CLASS_NAME).equal(interfaceName)
					.field(__PACKAGE_NAME).equal(packageName)
					.field(__IS_INTERFACE).equal(true);
			tmp = q.asList();
		}
		output.addAll(tmp);
		//first collect all the interfaces that extends the current one
		q = datastore.createQuery(IndexData.class)
				.field(__IS_INTERFACE).equal(true)
				.field(__IMPLEMENTS).contains(interfaceName);
		q.or(
				q.or(
						q.criteria(__IMPORTS).equal(packageName + ".*"),
						q.criteria(__IMPORTS).equal(packageName + "." + interfaceName)
				),
				q.criteria(__PACKAGE_NAME).equal(packageName)
		);
		tmp = q.asList();
		output.addAll(tmp);
		tmp.clear();
		//for each of it collect the class that implements it
		for(IndexData intf : output){
			q = datastore.createQuery(IndexData.class)
					.field(__IS_INTERFACE).equal(false)
					.field(__IMPLEMENTS).contains("(.?)" + intf.getName());
			if(Character.isUpperCase(intf.getFullclassPackage().substring(intf.getFullclassPackage().lastIndexOf(".")+1).charAt(0))){
				q.or(
						q.criteria(__IMPORTS).contains(intf.getFullclassPackage() + ".*"),
						q.criteria(__IMPORTS).contains(intf.getFullclassPackage() + "." + intf.getName()),
						q.criteria(__PACKAGE_NAME).equal(intf.getClassPackage())
				);
			} else {
				q.or(
						q.criteria(__IMPORTS).equal(intf.getClassPackage() + ".*"),
						q.criteria(__IMPORTS).equal(intf.getClassPackage() + "." + intf.getName()),
						q.criteria(__PACKAGE_NAME).equal(intf.getClassPackage())
				);
			}

			tmp.addAll( q.asList() );
		}
		output.addAll(tmp);
		//for each result collect the extensions of them until no one extended it anymore
		tmp.clear();
		int numNewInsert = output.size();
		List<IndexData> tmp_output = new ArrayList<>(output);
		while(numNewInsert != 0) {
			tmp.clear();
			for (IndexData data : tmp_output) {
				if (data.isInterface()) continue;
				tmp.addAll( getExtensionOfClass(data.getClassPackage(), data.getName()) );
			}
			numNewInsert = 0;
			tmp_output.clear();
			for(IndexData elm : tmp){
				if(!output.contains(elm)){
					numNewInsert++;
					output.add(elm);
					tmp_output.add(elm);
				}
			}
		}
		cacheInterface.put(p, output);
		return output;
	}

	public synchronized List<IndexData> getExtensionOfClass(String pkg, String name){
		Pair<String,String> p = new Pair<>(pkg, name);
		if(cacheExtended.containsKey(p)){
			return cacheExtended.get(p);
		}
		Query<IndexData> q;
		q = datastore.createQuery(IndexData.class)
				.field(__IS_INTERFACE).equal(false)
				.field(__EXTENDED).equal(name);
		q.or(
				q.criteria(__IMPORTS).equal(pkg + ".*"),
				q.criteria(__IMPORTS).equal(pkg + "." + name),
				q.criteria(__PACKAGE_NAME).equal(pkg)
		);
		List<IndexData> out =  q.asList();
		cacheExtended.put(p, out);
		return out;
	}

	public synchronized void setIndexStart(){
		try {
			this.dbStatus.setLastEdit(new Timestamp(System.currentTimeMillis()));
			this.dbStatus.setIndexed(false);
			this.datastore.save(dbStatus);
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}

	public synchronized void setIndexFinish(){
		try {
			this.dbStatus.setLastEdit(new Timestamp(System.currentTimeMillis()));
			this.dbStatus.setIndexed(true);
			this.datastore.save(dbStatus);
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}

	public synchronized boolean getIndexStatus(){
		Query<DBStatus> q = datastore.createQuery(DBStatus.class);
		q.field("dbName").equal(dbName);
		try {
			List<DBStatus> r = q.asList();
			if (!(r.size() > 0)) {
				dbStatus = new DBStatus(dbName, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), false);
				datastore.save(dbStatus);
			} else {
				dbStatus = r.get(0);
			}
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
		return this.dbStatus.isIndexed();
	}

	public synchronized List<IndexData> getType(String type) {
		Query<IndexData> q;
		q = datastore.createQuery(IndexData.class);
		q.or(
				q.criteria(__EXTENDED).equal(type),
				q.criteria(__IMPLEMENTS).contains(type)
		);
		return q.asList();
	}


	/**
	 * Delete the current database
	 */
	public synchronized void drop(){
		this.db.drop();
		this.dbStatus = new DBStatus(dbName, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), false);
		this.cacheIndex = new HashMap<>();
		this.cacheImport = new HashMap<>();
		this.cacheSyncBlock = new HashMap<>();
		this.cacheSyncCall = new HashMap<>();
	}


	public synchronized void deleteSyncCalls(ASTClass c) {
		Query<IndexSyncCall> q = datastore.createQuery(IndexSyncCall.class)
				.field(__CLASS_NAME).equal(c.getName())
				.field(__PACKAGE_NAME).equal(c.getPackageName());
		datastore.delete(q);
		Pair<String,String> p = new Pair<>(c.getName(),c.getPackageName());
		cacheSyncCall.remove(p);
	}

	public synchronized void deleteSyncBlock(ASTClass c) {
		Query<IndexSyncBlock> q = datastore.createQuery(IndexSyncBlock.class)
				.field(__CLASS_NAME).equal(c.getName())
				.field(__PACKAGE_NAME).equal(c.getPackageName());
		datastore.delete(q);
		Pair<String,String> p = new Pair<>(c.getName(),c.getPackageName());
		cacheSyncBlock.remove(p);
	}

	public synchronized boolean existIndexSocketClass(ASTClass c) {
		return existIndexSocketClass(c.getPath());
	}

	private boolean existIndexSocketClass(String path) {
		return getIndexSocketClass(path).size() > 0;
	}

	public synchronized void add(IndexSocket s) {
		if(cacheSocket.containsKey(s.getFullName())){
			return;
		}
		try {
			datastore.save(s);
			cacheSocket.put(s.getFullName(), s);
		} catch (BsonSerializationException e){
			System.err.println("File too big, cannot handle it!");
		} finally {
			cacheSocketClass.remove(s.getFilename());
		}
	}

	public synchronized List<IndexSocket> getIndexSocketClass(String path) {
		if(cacheSocketClass.containsKey(path)){
			return cacheSocketClass.get(path);
		}
		List<IndexSocket> out = datastore.createQuery(IndexSocket.class)
				.field(__FILE).equal(path)
				.asList();
		if(out.size() > 0)
			cacheSocketClass.put(path, out);
		return out;
	}

	public synchronized List<IndexSocket> getIndexSocketClass(ASTClass c) {
		return getIndexSocketClass(c.getPath());
	}
}