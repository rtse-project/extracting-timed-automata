package server;

import com.sun.net.httpserver.HttpServer;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import server.handler.*;
import server.handler.test.echoGet;
import server.handler.test.echoHeader;
import server.handler.test.echoPost;
import server.routes.v1.Routes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class HttpServerConverter {



	static int port = 9000;
	private final int noOfThreads = 4;
	private final ExecutorService httpThreadPool;
	HttpServer server;
	public static boolean _debug;

	private static Map<String,String> lastParameters = new HashMap<>();

	public static int getPort() {
		return port;
	}

	public HttpServerConverter() throws IOException {
		this(port);
	}

	public HttpServerConverter(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		System.out.println("server started at " + port);


		httpThreadPool = Executors.newFixedThreadPool(this.noOfThreads);

		//Test urls
		server.createContext("/", new root());
		server.createContext(Routes.SHUTDOWN, new shutDown());
		server.createContext("/test/echoHeader", new echoHeader());
		server.createContext("/test/echoGet", new echoGet());
		server.createContext("/test/echoPost", new echoPost());


		//Get all java files from a project
		server.createContext(Routes.GET_ALL_FILES, new getAllFiles());
		server.createContext(Routes.GET_FILE, new getFile());


		openProject op = new openProject();
		//Start project index
		server.createContext(Routes.OPEN_PROJECT, 		op);
		server.createContext(Routes.IS_PROJECT_OPEN, 	new isProjectOpen());
		server.createContext(Routes.GET_FILE_BY_TYPE, 	new getFilesByType());
		server.createContext(Routes.GET_THREADS, 		new getThreads());
		server.createContext(Routes.GET_STATUS, 		new getStatus(op));
		server.createContext(Routes.GET_MAINS, 			new getMains());
		server.createContext(Routes.CLEAN, 				new clean(op));
		server.createContext(Routes.CLEAN_ALL, 			new cleanAll(op));

		server.setExecutor(httpThreadPool);
		server.start();
	}

	public static void parseQuery(String query, Map<String,String> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0],
							System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1],
							System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);

					}
				} else {
					parameters.put(key, value);
				}
			}
		}
		lastParameters = parameters;
	}

	public static Map<String, String> getLastParameters() {
		return lastParameters;
	}

	public static boolean isDebugActive() {
		return _debug;
	}

	public void stop() {
		System.out.println("server stopped at " + port);
		closeDB();
		server.stop(1);
		httpThreadPool.shutdownNow();
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			//we try but who cares
		}
	}

	private void closeDB(){
		MongoConnector mongo = MongoConnector.getInstance();
		List<String> dbs = mongo.databases();
		for(String db : dbs) {
			MongoConnector c = MongoConnector.getInstance(db);
			c.close();
		}
		mongo.close();
	}

	public void setDebug(boolean debug) {
		_debug = debug;
	}
}
