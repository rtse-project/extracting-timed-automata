package server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import org.apache.commons.io.FileUtils;
import server.HttpServerConverter;
import server.handler.middleware.BaseRoute;
import server.handler.middleware.ParsePars;
import server.handler.middleware.indexMW;

import java.io.*;
import java.util.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class getAllFiles extends indexMW {

	String par1 = "skipTest";

	@Override
	protected void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {
		//validate input: expect 2 pars projectPath&skipTest
		boolean flag = true;
		if(!parameters.containsKey(par1)){
			flag = false;
		}
		if(!flag){
			ParsePars.printErrorMessagePars(he);
			return;
		}
		boolean skipTest = parameters.get(par1).equals("1");

		MongoConnector mongo = MongoConnector.getInstance(name);
		String base_path = mongo.getBasePath();

		base_path = base_path.replace("file://","");
		//Compute response
		File dir = new File(base_path);
		String[] filter = {"java"};
		Collection<File> files = null;
		try{
			files = FileUtils.listFiles(
					dir,
					filter,
					true
			);
		}
		catch (Exception e){
			String response = "Directory " + base_path + " does not exists or it cannot be opened";
			he.sendResponseHeaders(400, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
			return;
		}
		Iterator i = files.iterator();
		List<String> outputFiles = new ArrayList<>();

		while (i.hasNext()) {

			String filename = ((File)i.next()).getAbsolutePath();
			if(skipTest && filename.contains("/test")){
				continue;
			}
			//remove project path
			outputFiles.add(filename.replace(base_path,""));
		}

		// send response
		ObjectMapper json = ParsePars.getOutputFormat(parameters);
		String response = json.writeValueAsString(outputFiles);
		he.getResponseHeaders().add("Content-Type","application/json");
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

}
