package server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import org.javatuples.Pair;
import server.handler.middleware.ParsePars;
import server.handler.middleware.indexMW;
import server.handler.outputFormat.Status;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class openProject extends indexMW {

	static final Object lock = new Object();
	String lastProjectOpen = "";

	class IndexingThread extends Thread {
		String name;
		String base_path;
		boolean delete;
		HashMap<String, Pair<Boolean,String>> indexProcess;

		public IndexingThread(String name, String base_path, boolean delete, HashMap<String, Pair<Boolean,String>> indexProcess) {
			super();
			this.name = name;
			this.base_path = base_path;
			this.delete = delete;
			this.indexProcess = indexProcess;
		}

		@Override
		public void run() {
			boolean flag;
			synchronized (lock){
				flag = indexProcess.containsKey(name) ? indexProcess.get(name).getValue0() : true;
				indexProcess.put(name, new Pair<>(true, base_path));
			}
			if(flag) {
				IndexingProject index = new IndexingProject(name);
				index.setSkipTest(false);
				index.indexProject(base_path, delete);
			}
			synchronized (lock){
				indexProcess.put(name, new Pair<>(false, base_path));
			}
		}
	}


	String par1 = "path";
	String par2 = "invalidCache";

	HashMap<String, Pair<Boolean,String>> indexProcess = new HashMap<>();
	public void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {

		MongoConnector mongo = MongoConnector.getInstance(name);
		String base_path = "";
		//is path mandatory?
		if(indexProcess.containsKey(name) && mongo.getBasePath() != null){
			//no it is not
			base_path = indexProcess.get(name).getValue1();
		} else if(mongo.getIndexStatus()) {
			base_path = mongo.getBasePath();
		} else {
			//yes it is
			boolean flag = true;
			if(!parameters.containsKey(par1)){
				flag = false;
			}
			if(!flag){
				ParsePars.printErrorMessagePars(he, "Expected the parameter `path`");
				return;
			}
			base_path = parameters.get(par1);
			if(!ParsePars.parseFileUrl(base_path, he)){
				return;
			}
		}
		//if path is defined as parameter we MUST use it rewriting previous values
		if(parameters.containsKey(par1)){
			base_path = parameters.get(par1);
		}
		boolean delete = parameters.containsKey(par2) && parameters.get(par2).equals("1");
		base_path = base_path.replace("file://","");

		Status msg;
		//does the path exists?
		if (new File(base_path).exists()) {
			boolean doesItExistsAlready = false;
			synchronized (lock){
				doesItExistsAlready = indexProcess.containsKey(name) && indexProcess.get(name).getValue0();
			}
			if(!doesItExistsAlready) {
				IndexingThread thread = new IndexingThread(name, base_path, delete, indexProcess);
				thread.start();
				//compute
				msg = new Status("0", "");
			} else {
				msg = new Status("1", "A process is already parsing this folder");
			}
		} else {
			msg = new Status("1", "Path does not exists");
		}

		lastProjectOpen = base_path;

		ObjectMapper json = ParsePars.getOutputFormat(parameters);
		json.enable(SerializationFeature.INDENT_OUTPUT);
		String response = json.writeValueAsString(msg);
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

	public boolean doesItExists(String name){
		boolean doesItExistsAlready = false;
		synchronized (lock){
			doesItExistsAlready = indexProcess.containsKey(name) && indexProcess.get(name).getValue0();
		}
		return doesItExistsAlready;
	}

	protected void delete(String name){
		synchronized (lock){
			if(indexProcess.containsKey(name)) {
				indexProcess.remove(name);
			}
		}
	}

	@Override
	protected void printLog() {
		super.printLog();
		System.out.println("\tOpening project: " + lastProjectOpen);
	}
}
