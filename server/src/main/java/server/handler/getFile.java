package server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTRE;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModel.visitors.interfaces.ParseIM;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.heuristic.definition.TimeInSignature;
import intermediateModelHelper.heuristic.definition.AssignmentTimeVar;
import intermediateModelHelper.heuristic.definition.TimeoutResources;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import server.HttpServerConverter;
import server.handler.middleware.ParsePars;
import server.handler.middleware.indexMW;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class getFile extends indexMW {
	String par1 = "filePath";

	String lastFileServed = "";

	private static final Logger LOGGER = LogManager.getRootLogger();

	{
		Configurator.setRootLevel( HttpServerConverter.isDebugActive() ? Level.ALL : Level.OFF);
	}

	class AnnotateEnv extends ParseIM {
		@Override
		public void start(ASTClass c) {
			super.start(c);
		}

		@Override
		protected void analyzeASTRE(ASTRE r, Env env) {
			super.analyzeASTRE(r, env);
			r.setEnv(env);
		}
	}

	@Override
	protected void handle(HttpExchange he, Map<String, String> parameters, String name) throws IOException {
		LOGGER.debug("Request getFile on {} parameters: [{}]", name, parameters);
		//validate input
		boolean flag = true;
		if(!parameters.containsKey(par1)){
			flag = false;
		}
		if(!flag){
			ParsePars.printErrorMessagePars(he, "Expected `" + par1 + "`");
			return;
		}
		String file_path = parameters.get(par1);
		if(!ParsePars.parseFileUrl(file_path, he)){
			return;
		}
		file_path = file_path.replace("file://","");

		//get base path of project
		MongoConnector mongo = MongoConnector.getInstance(name);
		String base_path = mongo.getBasePath();

		String file = base_path + "/" + file_path;
		//avoid bad path
		file = file.replace("//","/");
		lastFileServed = file;


		List<ASTClass> classes;
		//Compute response
		try {
			classes = JDTVisitor.parse(file, base_path);
		} catch (Exception e){
			LOGGER.debug(e);
			String response = "File not found!";
			he.sendResponseHeaders(400, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
			return;
		}
		//annotate with env and time
		for(ASTClass c : classes){
			AnnotateEnv a = new AnnotateEnv();
			a.start(c);
			ApplyHeuristics ah = new ApplyHeuristics();
			ah.subscribe(TimeInSignature.class);
			ah.subscribe(TimeoutResources.class);
			ah.subscribe(AssignmentTimeVar.class);

			ah.analyze(c);
			//annotate each method
			for(IASTMethod m : c.getMethods()){
				m.setDeclaredVars();
			}
			for(Constraint cnst : ah.getTimeConstraint()){
				cnst.removeElm();
			}
			c.setPath(file_path);
		}
		//annotate with Time


		// send response
		ObjectMapper json = ParsePars.getOutputFormat(parameters);
		json.enable(SerializationFeature.INDENT_OUTPUT);

		String response = "";
		try {
			response = json.writeValueAsString(classes);
		} catch (Exception e){
			LOGGER.catching(e);
		}
		//LOGGER.debug(response);
		he.getResponseHeaders().add("Content-Type","application/json");
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	@Override
	protected void printLog() {
		super.printLog();
		System.out.println("File served: " + lastFileServed);
	}
}
