package server.handler.test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.HttpServerConverter;
import server.handler.middleware.BaseRoute;

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
public class echoPost extends BaseRoute {

	@Override
	public void handleConnection(HttpExchange he) throws IOException {
		// parse request
		Map<String, String> parameters = new HashMap<>();
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		HttpServerConverter.parseQuery(query, parameters);

		// send response
		String response = "";
		for (String key : parameters.keySet())
			response += key + " = " + parameters.get(key) + "\n";
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}


}
