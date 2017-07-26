package server;

import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import server.helper.PropertiesFileReader;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class StartServer {

	static boolean debug = false;

	public static void main(String[] args) throws Exception {
		int port = HttpServerConverter.port;
		int i = 0;

		while(i < args.length){
			String current = args[i++];
			switch (current){
				case "-port":
					port = Integer.parseInt(args[i++]);
					break;
				case "-debug":
					debug = true;
					break;
			}
		}

		System.out.println("Version : " + PropertiesFileReader.getGitSha1());

		Callable<Boolean> task = () -> {
			try {
				MongoConnector c = MongoConnector.getInstance();
				c.getDb();
				return true;
			}
			catch (Exception e) {
				return false;
			}
		};
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Boolean> future = executor.submit(task);
		String clear = "\r" +  String.join("", Collections.nCopies(25, " "));
		i = -1;
		while(!future.isDone()){
			Thread.sleep(500);
			System.out.print(clear);
			System.out.print("\rTry to contact mongodb" + String.join("", Collections.nCopies((++i)%3 + 1, ".")) );
		}
		Boolean result = future.get();
		if(result) {
			System.out.println("\nMongo was found!");
			new StartServer().run(port);
		}
		else {
			System.err.println("Cannot contact mongodb. Please run mongodb before calling the server.");
			System.err.flush();
			System.exit(1);
		}
	}

	public void run(int port) throws IOException {
		HttpServerConverter._debug = debug;
		HttpServerConverter server = new HttpServerConverter(port);
		//server.setDebug(debug);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
	}
}
