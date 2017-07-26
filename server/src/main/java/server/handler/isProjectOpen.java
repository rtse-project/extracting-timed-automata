package server.handler;

import intermediateModelHelper.indexing.IndexingProject;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import com.sun.net.httpserver.HttpExchange;
import server.handler.middleware.indexMW;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class isProjectOpen extends indexMW {


	public void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {

		MongoConnector connector = MongoConnector.getInstance(name);
		int retVal = connector.getIndexStatus() ? 1 : 0;
		String response = "{\n\t\"status\": \"" + retVal + "\"\n}\n";
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

}
