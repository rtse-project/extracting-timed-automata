package intermediateModelHelper.heuristic.definition;


import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefaultASTVisitor;
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
public class SetTimeoutPermissive extends SearchTimeConstraint {

	List<TimeTimeout> timeTimeout = TemporalInfo.getInstance().getTimeTimeout();
	List<TimeTimeout> readTimeout = TemporalInfo.getInstance().getReadTimeout();

	boolean isSetReadTimeoutDefined = false;
	boolean isSetTimeoutDefined = false;


	private class SetTimeOutMethod extends TimeInfo {
		public SetTimeOutMethod(String className, String methodName, List<String> signature) {
			super(className, methodName, signature);
		}
	}

	private List<SetTimeOutMethod> timeOutMethod = new ArrayList<>();
	private List<SetTimeOutMethod> timeOutReadMethod = new ArrayList<>();

	@Override
	public void setup(ASTClass c) {
		super.setup(c);
		this.isSetReadTimeoutDefined = false;
		this.isSetTimeoutDefined = false;

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


		//search if a timeout read or timeout is defined in the class

		c.visit(new DefaultASTVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				if(isSetTimoutRead(elm)){
					isSetReadTimeoutDefined = true;
				}
				if(isSetTimout(elm)){
					isSetTimeoutDefined = true;
				}
			}
		});

	}


	/**
	 * The search accept only {@link ASTRE}, in particular it checks only {@link ASTMethodCall}. <br>
	 * It collects the {@link ASTMethodCall} in the RExp and search for the definition of the method <b>setSoTimeout</b>
	 * from which it extracts the time value. Then when it finds the call to <b>receive</b> or <b>getOutputStream</b>
	 * it saves the time constraint (if the variable that calls the methods is time relevant as well).
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
				//is require an input stream which has a timeout?
				if(isReadingInputStream(elm) && isSetReadTimeoutDefined){
					SetTimeoutPermissive.super.addConstraint("timeout", elm, true);
				}
				//check if it calls a method with timeout
				if(requireSetTimout(elm) && isSetTimeoutDefined){
					SetTimeoutPermissive.super.addConstraint("timeout", elm, true);
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
