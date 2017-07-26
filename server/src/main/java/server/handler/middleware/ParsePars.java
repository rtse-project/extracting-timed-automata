package server.handler.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.net.httpserver.HttpExchange;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by giovanni on 02/03/2017.
 */
public class ParsePars {

    public static boolean ParseIndexStatus(String name, HttpExchange he) throws IOException {
        MongoConnector db = MongoConnector.getInstance(name);
        if(!db.getIndexStatus()){
            String response = "Indexes are not yet computed.";
            he.sendResponseHeaders(406, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
            return false;
        }
        return true;
    }

    public static ObjectMapper getOutputFormat(Map<String, String> parameters) {
        if(parameters.containsKey("format") && parameters.get("format").equals("yaml")){
            return new ObjectMapper(new YAMLFactory());
        }
        return new ObjectMapper();
    }

    public static void printErrorMessagePars(HttpExchange he) throws IOException {
        printErrorMessagePars(he, "");
    }
    public static void printErrorMessagePars(HttpExchange he, String response) throws IOException {
        response = "Incorrect format for the parameters." + response;
        he.sendResponseHeaders(400, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    public static boolean parseFileUrl(String base_path, HttpExchange he) throws IOException {
        if(!base_path.startsWith("file://")){
            String response = "Unsupported protocol. atm only file:// is supported.";
            he.sendResponseHeaders(400, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
            return false;
        }
        return true;
    }
}
