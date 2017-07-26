package server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import server.handler.middleware.ParsePars;
import server.handler.middleware.indexMW;
import server.handler.outputFormat.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by giovanni on 07/03/2017.
 */
public class clean extends indexMW {

    openProject op = null;

    public clean(openProject op) {
        this.op = op;
    }

    @Override
    protected void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {
        MongoConnector mongo = MongoConnector.getInstance(name);
        mongo.drop();

        //remove the project
        op.delete(name);

        Status ok = new Status("0","");
        ObjectMapper json = ParsePars.getOutputFormat(parameters);
        json.enable(SerializationFeature.INDENT_OUTPUT);
        String response = json.writeValueAsString(ok);
        he.getResponseHeaders().add("Content-Type","application/json");
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
