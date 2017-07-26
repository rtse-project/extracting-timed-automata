package server.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import server.HttpServerConverter;
import server.handler.middleware.BaseRoute;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class root extends BaseRoute {

	@Override
	public void handleConnection(HttpExchange he) throws IOException {

		Headers headers = he.getRequestHeaders();
		Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
		boolean user = false;
		for (Map.Entry<String, List<String>> entry : entries)
			if (entry.getKey().equals("Accept-encoding"))
				user = true;

		if (user) {
			ClassLoader classLoader = getClass().getClassLoader();

			StringWriter writer = new StringWriter();
			IOUtils.copy(classLoader.getResource("index.html").openStream(), writer);

			StringWriter md = new StringWriter();
			IOUtils.copy(classLoader.getResource("doc.md").openStream(), md);

			String response = writer.toString();
			String doc = md.toString();
			response = response.replace("{MD}", doc);

			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else {
			String response = "The connection works. But your request was not correct.";
			he.sendResponseHeaders(400, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
}
