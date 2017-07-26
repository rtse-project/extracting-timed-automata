package intermediateModelHelper.indexing;

import com.google.common.collect.Iterators;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.envirorment.temporal.CollectReturnTimeMethods;
import intermediateModelHelper.envirorment.temporal.structure.TimeTypes;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The following class analyze a project and stores in MongoDB the indexing of it.
 * It creates a database per project. If a class name already exists in the database is not added.
 * Two classes are equals if they share the same name and packaging.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IndexingProject {
	protected MongoConnector db;
	protected String projectName;
	protected boolean skipTest = true;
	protected boolean showUpdates = false;

	/**
	 * Construct the db given the project name.
	 * @param name	Project Name
	 */
	public IndexingProject(String name) {
		this.db = MongoConnector.getInstance(name);
		this.projectName = name;
	}

	/**
	 * Construct the db given the standard project name defined in {@link MongoOptions}.
	 */
	public IndexingProject() {
		String name = MongoOptions.getInstance().getDbName();
		this.db = MongoConnector.getInstance(name);
		this.projectName = name;
	}

	public boolean isSkipTest() {
		return skipTest;
	}

	public void setSkipTest(boolean skipTest) {
		this.skipTest = skipTest;
	}

	public String getProjectName() {
		return projectName;
	}

	public boolean isShowUpdates() {
		return showUpdates;
	}

	public void setShowUpdates(boolean showUpdates) {
		this.showUpdates = showUpdates;
	}

	/**
	 * Start the indexing from the <b>base_path</b> passed as parameter.
	 * It iterates on the directory and sub-directories searching for Java files.
	 * @param base_path	Path from where start to search for Java files.
	 * @return The number of file parsed. <u>IT IS NOT THE NUMBER OF CLASSES INSERTED IN THE DATABASE</u>
	 */
	public int indexProject(String base_path) {
		return indexProject(base_path, false);
	}

	/**
	 * Start the indexing from the <b>base_path</b> passed as parameter.
	 * It iterates on the directory and sub-directories searching for Java files.
	 * @param base_path	Path from where start to search for Java files.
	 * @param deleteOld	If true delete the database so we have fresh data.
	 * @return	The number of file parsed. <u>IT IS NOT THE NUMBER OF CLASSES INSERTED IN THE DATABASE</u>
	 */
	public int indexProject(String base_path, boolean deleteOld){
		//remove old data
		db.setIndexStart();
		if(deleteOld) delete();
		Iterator i = getJavaFiles(base_path);
		int size = 0;
		if(this.showUpdates){
			size = Iterators.size(i);
			i = getJavaFiles(base_path);
		}
		int n_file = 0;
		while (i.hasNext()) {
			if(this.showUpdates){
				System.out.print("\r                                                 ");
				System.out.print(String.format("\r[DEBUG] Indexing %f %%", (double)n_file/(double)size));
			}
			String filename = ((File)i.next()).getAbsolutePath();
			if(this.skipTest && filename.contains("/test")){
				continue;
			}
			List<ASTClass> result = JDTVisitor.parse(filename, base_path);
			//pp filename
			for(ASTClass c : result){
				IndexingFile indexing = new IndexingFile(db);
				indexing.index(c);
				//db.add(index);
			}
			n_file++;
		}
		if(this.showUpdates){
			System.out.println("");
		}
		//ensure indexes
		db.ensureIndexes();
		db.setIndexFinish();
		db.setBasePath(base_path);
		return n_file;
	}

	/**
	 * Start the indexing from the <b>base_path</b> passed as parameter.
	 * It iterates on the directory and sub-directories searching for Java files.
	 * @param base_path	Path from where start to search for Java files.
	 * @return	The number of file parsed. <u>IT IS NOT THE NUMBER OF CLASSES INSERTED IN THE DATABASE</u>
	 */
	@Deprecated
	public int indexSyncCall(String base_path, boolean deleteOld){
		if(deleteOld) delete();
		Iterator i = getJavaFiles(base_path);
		int n_file = 0;
		while (i.hasNext()) {
			String filename = ((File)i.next()).getAbsolutePath();
			if(this.skipTest && filename.contains("/test")){
				continue;
			}
			List<ASTClass> result = JDTVisitor.parse(filename);
			//pp filename
			for(ASTClass c : result){
				IndexingSyncCalls indexing = new IndexingSyncCalls(db);
				indexing.index(c);
				//db.add(index);
			}
			n_file++;
		}
		//ensure indexes
		db.ensureIndexes();
		return n_file;
	}

	/**
	 * Start the indexing from the <b>base_path</b> passed as parameter.
	 * It iterates on the directory and sub-directories searching for Java files.
	 * @param base_path	Path from where start to search for Java files.
	 * @return	The number of file parsed. <u>IT IS NOT THE NUMBER OF CLASSES INSERTED IN THE DATABASE</u>
	 */
	@Deprecated
	public int indexSyncBlock(String base_path, boolean deleteOld){
		if(deleteOld) delete();
		Iterator i = getJavaFiles(base_path);
		int n_file = 0;
		while (i.hasNext()) {
			String filename = ((File)i.next()).getAbsolutePath();
			if(this.skipTest && filename.contains("/test")){
				continue;
			}
			List<ASTClass> result = JDTVisitor.parse(filename);
			for(ASTClass c : result){
				IndexingSyncBlock indexing = new IndexingSyncBlock(db);
				indexing.index(c);
				//db.add(index);
			}
			n_file++;
		}
		//ensure indexes
		db.ensureIndexes();
		return n_file;
	}

	public static Iterator<File> getJavaFiles(String base_path){
		File dir = new File(base_path);
		String[] filter = {"java"};
		Collection<File> files = FileUtils.listFiles(
				dir,
				filter,
				true
		);
		Iterator<File> i = files.iterator();
		return i;
	}

	public static List<TimeTypes> getMethodReturnTime(String name, String base_path, boolean save){
		File dir = new File(base_path);
		String[] filter = {"java"};
		Collection<File> files = FileUtils.listFiles(
				dir,
				filter,
				true
		);
		Iterator<File> i = files.iterator();
		List<TimeTypes> out = new ArrayList<>();
		CollectReturnTimeMethods collectReturnTimeMethods = new CollectReturnTimeMethods(save, name);
		while (i.hasNext()) {
			String filename = i.next().getAbsolutePath();
			if(filename.contains("/src/test/"))
				continue;
			List<ASTClass> result = JDTVisitor.parse(filename, base_path);
			for(ASTClass c : result){
				out.addAll(collectReturnTimeMethods.index(c));
			}
		}
		return out;
	}

	/**
	 * Start the indexing from the <b>base_path</b> passed as parameter.
	 * It iterates on the directory and sub-directories searching for Java files.
	 * @param base_path	Path from where start to search for Java files.
	 * @param delete	If true delete the database so we have fresh data.
	 * @return			A {@link Future} that will contain soon or late the number of file parsed. <u>IT WILL NOT RETURN THE NUMBER OF CLASSES INSERTED IN THE DATABASE</u>
	 */
	public Future<Integer> asyncIndexProject(String base_path, boolean delete){
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Callable<Integer> task = () -> indexProject(base_path, delete);
		return executor.submit(task);
	}
	/**
	 * Start the indexing from the <b>base_path</b> passed as parameter.
	 * It iterates on the directory and sub-directories searching for Java files.
	 * @param base_path	Path from where start to search for Java files.
	 * @return			A {@link Future} that will contain soon or late the number of file parsed. <u>IT WILL NOT RETURN THE NUMBER OF CLASSES INSERTED IN THE DATABASE</u>
	 */
	public Future<Integer> asyncIndexProject(String base_path){
		return asyncIndexProject(base_path, false);
	}


	public void delete(){
		db.drop();
	}

}
