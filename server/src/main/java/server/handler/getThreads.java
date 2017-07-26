package server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.structure.IndexData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.handler.middleware.ParsePars;
import server.handler.middleware.indexMW;
import server.handler.outputFormat.OutputData;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class getThreads extends indexMW {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	protected void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {
		LOGGER.debug("Request getThread on {} parameters: [{}]", name, parameters);
		if(!ParsePars.ParseIndexStatus(name,he)){
			return;
		}
		MongoConnector db = MongoConnector.getInstance(name);
		String base_path = db.getBasePath();
		List<IndexData> threads = db.getThreads();
		List<OutputData> files = new ArrayList<>();
		for(IndexData d : threads){
			if(! files.contains(d.getPath()) )
				files.add(new OutputData(d, base_path));
		}
		ObjectMapper json = ParsePars.getOutputFormat(parameters);
		json.enable(SerializationFeature.INDENT_OUTPUT);
		String response = json.writeValueAsString(files);
		he.getResponseHeaders().add("Content-Type","application/json");
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();

	}

}
