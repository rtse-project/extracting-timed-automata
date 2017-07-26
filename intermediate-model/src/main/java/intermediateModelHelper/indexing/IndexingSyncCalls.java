package intermediateModelHelper.indexing;

import intermediateModel.structure.ASTClass;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexSyncCall;
import intermediateModelHelper.indexing.structure.SyncMethodCall;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class created the {@link IndexSyncCall} for a given {@link ASTClass}.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IndexingSyncCalls  {

	MongoConnector mongo;
	ASTClass _c = null;

	public IndexingSyncCalls() {
		String dbname = MongoOptions.getInstance().getDbName();
		mongo = MongoConnector.getInstance(dbname);
	}

	public IndexingSyncCalls(MongoConnector mongo) {
		this.mongo = mongo;
	}

	/**
	 * Start the indexing the calls of synchronized method of a {@link ASTClass}.
	 * It force to delete the index structure from the DB and recreate it.
	 *
	 * @param c	Class to analyse.
	 * @return	The list of indexes of sync calls of the class.
	 */
	public List<IndexSyncCall> index(ASTClass c){
		return index(c, false);
	}
	/**
	 * Start the indexing the calls of synchronized method of a {@link ASTClass}.
	 * It force to delete the index structure from the DB and to recreate it if the <i>forceReindex</i> flag is set to true.
	 * @param c	Class to analyze
     * @param forceReindex flag to force the recreation of the index
	 * @return	The list of indexes of sync calls of the class.
	 */
	public List<IndexSyncCall> index(ASTClass c, boolean forceReindex) {
		this._c = c;
		//System.out.println(c.getPath());
		//collect calls to a sync method
		GenerateMethodSyncCallList syncCalls = new GenerateMethodSyncCallList(c, c.getMethods());
		List<SyncMethodCall> calls = syncCalls.calculateSyncCallList();
		List<IndexSyncCall> out = new ArrayList<>();
		for(SyncMethodCall call : calls){
			IndexSyncCall index = new IndexSyncCall(call, c.getPath());
			mongo.add(index);
			out.add(index);
		}
		return out;
	}
}