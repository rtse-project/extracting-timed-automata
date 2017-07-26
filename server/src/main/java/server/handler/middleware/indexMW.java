package server.handler.middleware;

import com.sun.net.httpserver.HttpExchange;
import server.HttpServerConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class indexMW extends BaseRoute {



	@Override
	public void handleConnection(HttpExchange he) throws IOException {

		Map<String, String> parameters = new HashMap<>();
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		HttpServerConverter.parseQuery(query, parameters);

		if(!parameters.containsKey("name")){
			String response = "Project name is not specified.";
			he.sendResponseHeaders(406, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
			return;
		}
		String name = parameters.get("name");
		handle(he, parameters, name);
	}



	protected abstract void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException;
}
