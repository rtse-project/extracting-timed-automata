package intermediateModelHelper.heuristic.definition;


import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTConstructor;
import intermediateModel.structure.ASTMethod;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTLiteral;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeTimeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * The class implement a search for the setSoTime out.
 * In socket programming the use of timeout is necessary to not wait endless messages that will be never dispatches.
 * An approach is to model these time constraint has a particular case of sleep.
 * The difference with the sleep is that the time constraint has not to be satisfied in the current state,
 * but it will be deferred to the <i>“receive”/"getOutputStream"</i> method call.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class SetTimeout extends SearchTimeConstraint {

	private boolean analysingConstructor;
	private boolean isReadingWithTimeoutConstruct;
	private boolean isReadingWithTimeout;


	List<TimeTimeout> timeTimeout = TemporalInfo.getInstance().getTimeTimeout();
	List<TimeTimeout> readTimeout = TemporalInfo.getInstance().getReadTimeout();



	private class SetTimeOutMethod extends TimeInfo {
		public SetTimeOutMethod(String className, String methodName, List<String> signature) {
			super(className, methodName, signature);
		}
	}

	private List<SetTimeOutMethod> timeOutMethod = new ArrayList<>();
	private List<SetTimeOutMethod> timeOutReadMethod = new ArrayList<>();
	private List<SetTimeOutMethod> inputStream = new ArrayList<>();

	List<IASTVar> socketVariables = new ArrayList<>();
	List<IASTVar> socketVariablesConstruct = new ArrayList<>();



	@Override
	public void setup(ASTClass c) {
		super.setup(c);
		this.analysingConstructor = false;
		this.isReadingWithTimeoutConstruct = false;
		this.isReadingWithTimeout = false;

		timeOutMethod.add( new SetTimeOutMethod("java.net.URLConnection", "setConnectTimeout", Arrays.asList("int") ));
		timeOutMethod.add( new SetTimeOutMethod("java.net.HttpURLConnection", "setConnectTimeout", Arrays.asList("int") ));
		timeOutMethod.add( new SetTimeOutMethod("javax.net.ssl.HttpsURLConnection", "setConnectTimeout", Arrays.asList("int") ));

		timeOutMethod.add( new SetTimeOutMethod("java.net.ServerSocket", "setSoTimeout", Arrays.asList("int") ));
		timeOutMethod.add( new SetTimeOutMethod("java.net.DatagramSocket", "setSoTimeout", Arrays.asList("int") ));
		timeOutMethod.add( new SetTimeOutMethod("java.net.MulticastSocket", "setSoTimeout", Arrays.asList("int") ));
		timeOutMethod.add( new SetTimeOutMethod("javax.net.ssl.SSLServerSocket", "setSoTimeout", Arrays.asList("int") ));

		timeOutReadMethod.add(new SetTimeOutMethod("java.net.Socket", "setSoTimeout", Arrays.asList("int") ));
		timeOutReadMethod.add(new SetTimeOutMethod("javax.net.ssl.SSLSocket", "setSoTimeout", Arrays.asList("int") ));
		timeOutReadMethod.add(new SetTimeOutMethod("java.net.URLConnection", "setReadTimeout", Arrays.asList("int") ));
		timeOutReadMethod.add(new SetTimeOutMethod("java.net.HttpURLConnection", "setReadTimeout", Arrays.asList("int") ));
		timeOutReadMethod.add(new SetTimeOutMethod("javax.net.ssl.HttpsURLConnection", "setReadTimeout", Arrays.asList("int") ));

		inputStream.add(new SetTimeOutMethod("java.net.Socket", "getInputStream", new ArrayList<>()));
		inputStream.add(new SetTimeOutMethod("javax.net.ssl.SSLSocket", "getInputStream", new ArrayList<>()));
		inputStream.add(new SetTimeOutMethod("java.net.URLConnection", "getInputStream", new ArrayList<>()));
		inputStream.add(new SetTimeOutMethod("java.net.HttpURLConnection", "getInputStream", new ArrayList<>()));
		inputStream.add(new SetTimeOutMethod("javax.net.ssl.HttpsURLConnection", "getInputStream", new ArrayList<>()));

	}

	@Override
	public void nextMethod(ASTMethod method, Env env) {
		super.nextMethod(method,env);
		this.analysingConstructor = false;
		this.isReadingWithTimeout = false;
		socketVariables.clear();
		socketVariables.addAll(socketVariablesConstruct);
	}

	@Override
	public void nextConstructor(ASTConstructor method, Env env) {
		super.nextConstructor(method,env);
		this.analysingConstructor = true;
	}

	/**
	 * The search accept only {@link ASTRE}, in particular it checks only {@link ASTMethodCall}. <br>
	 * It collects the {@link ASTMethodCall} in the RExp and search for the definition of the methods:
	 * <ul>
	 *     <li>setConnectTimeout</li>
	 *     <li>setSoTimeout</li>
	 *     <li>setReadTimeout</li>
	 * </ul>
	 * When a call of this method is detected, it sets the variable as <i>Time Related</i>.
	 * Whenever we then found that a time related variable calls a method which has a <b>time behaviour</b> we save the
	 * stm as time constraint.
	 *
	 * @param stm	Statement to process
	 * @param env	Envirorment visible to that statement
	 */
	@Override
	public void next(ASTRE stm, Env env) {
		IASTRE expr = stm.getExpression();
		if(expr == null){
			return;
		}

		expr.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				//check if is setting a timeout
				if(isSetTimout(elm)) {
					IASTVar var = getVar(elm, env);
					if(var != null) {
						if(analysingConstructor)
							socketVariablesConstruct.add(var);
						socketVariables.add(var);
					}
				}
				//check if set a timeout in read input stream
				if(isSetTimoutRead(elm)){
					if (analysingConstructor)
						isReadingWithTimeoutConstruct = true;
					isReadingWithTimeout = true;
				}
				//is require an input stream which has a timeout?
				if(isReadingInputStream(elm) && (isReadingWithTimeout || isReadingWithTimeoutConstruct)){
					SetTimeout.super.addConstraint("timeout", elm, true);
				}
				//check if it calls a method with timeout
				if(requireSetTimout(elm)){
					IASTVar var = getVar(elm, env);
					//check if variable was setted with timeout
					if(socketVariables.contains(var))
						SetTimeout.super.addConstraint("timeout", elm, true);
				}
			}
		});
	}

	private boolean isReadingInputStream(ASTMethodCall elm) {
		String pointer, name;
		int nPars;
		pointer = elm.getClassPointed();
		name = elm.getMethodName();
		nPars = elm.getParameters().size();
		if(pointer == null) return false;
		for(TimeTimeout m : readTimeout){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}

	private IASTVar getVar(ASTMethodCall elm, Env env){
		if(elm.getExprCallee() instanceof ASTLiteral){
			String varName = ((ASTLiteral) elm.getExprCallee()).getValue();
			IASTVar var = env.getVar(varName);
			return var;
		}
		return null;
	}

	private boolean isSetTimout(ASTMethodCall elm) {
		String pointer, name;
		int nPars;
		pointer = elm.getClassPointed();
		name = elm.getMethodName();
		nPars = elm.getParameters().size();
		if(pointer == null) return false;
		for(SetTimeOutMethod m : timeOutMethod){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}

	private boolean isSetTimoutRead(ASTMethodCall elm) {
		String pointer, name;
		int nPars;
		pointer = elm.getClassPointed();
		name = elm.getMethodName();
		nPars = elm.getParameters().size();
		if(pointer == null) return false;
		for(SetTimeOutMethod m : timeOutReadMethod){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}

	private boolean requireSetTimout(ASTMethodCall elm) {
		String pointer, name;
		int nPars;
		pointer = elm.getClassPointed();
		name = elm.getMethodName();
		nPars = elm.getParameters().size();
		if(pointer == null) return false;
		for(TimeTimeout m : timeTimeout){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}





}
