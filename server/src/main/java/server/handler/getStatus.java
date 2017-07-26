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
 * Created by giovanni on 03/03/2017.
 */
public class getStatus extends indexMW {

    private static final String _OPEN_ = "open";
    private static final String _OPENING_ = "opening";
    private static final String _CLOSED_ = "closed";

    openProject op;


    public getStatus(openProject op) {
        this.op = op;
    }

    @Override
    protected void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {
        //is project open?
        MongoConnector connector = MongoConnector.getInstance(name);
        Status status;
        if(connector.getIndexStatus()){
            //the project was opened successfully
            status = new Status(_OPEN_,"");
        } else if(op.doesItExists(name)){
            //is currently on going
            status = new Status(_OPENING_,"");
        } else {
            //never opened
            status = new Status(_CLOSED_,"");
        }
        ObjectMapper json = ParsePars.getOutputFormat(parameters);
        json.enable(SerializationFeature.INDENT_OUTPUT);
        String response = json.writeValueAsString(status);
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
