package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by giovanni on 08/03/2017.
 */
public class shutDown implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = "";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();

        System.exit(0);
    }
}
