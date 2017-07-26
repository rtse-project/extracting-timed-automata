package server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import server.HttpServerConverter;
import server.handler.middleware.BaseRoute;
import server.handler.middleware.ParsePars;
import server.handler.middleware.indexMW;
import server.handler.outputFormat.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giovanni on 07/03/2017.
 */
public class cleanAll extends BaseRoute {
    openProject op = null;

    public cleanAll(openProject op) {
        this.op = op;
    }

    @Override
    protected void handleConnection(HttpExchange he) throws IOException {

        Map<String, String> parameters = new HashMap<>();
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        HttpServerConverter.parseQuery(query, parameters);

        MongoConnector mongo = MongoConnector.getInstance();
        List<String> dbs = mongo.databases();

        for(String db : dbs) {
            MongoConnector c = MongoConnector.getInstance(db);
            c.drop();
            op.delete(db);
        }

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
