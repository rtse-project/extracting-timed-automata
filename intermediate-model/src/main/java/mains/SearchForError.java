package mains;

import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;


/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class SearchForError {

	public static void main(String[] args) throws Exception {
		String name = "_test_";
		MongoOptions.getInstance().setDbName(name);
		MongoConnector.getInstance(name).drop();

		String base_path = "/Users/giovanni/repository/sources/github/alluxio";

		IndexingProject indexingProject = new IndexingProject(name);
		indexingProject.indexProject(base_path, false);
	}
}
