package server.handler.test;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.handler.middleware.BaseRoute;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class echoHeader extends BaseRoute {



	@Override
	public void handleConnection(HttpExchange he) throws IOException {
		Headers headers = he.getRequestHeaders();
		Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
		String response = "";
		for (Map.Entry<String, List<String>> entry : entries)
			response += entry.toString() + "\n";
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

}
