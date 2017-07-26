package intermediateModelHelper.converter;


import XAL.XALStructure.XALAddState;
import XAL.XALStructure.exception.XALMalformedException;
import XAL.XALStructure.items.*;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * The class is responsible to convert the IM to the XAL representation.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class GenerateXAL {

	XALDocument xalDocument;
	XALState lastState = null;
	XALAddState lastAutomatonWhereAdd = null;
	XALAutomaton lastAutomaton = null;
	String metricValue = "";
	boolean isSwitch = false;
	List<XALState> switchLabels = new ArrayList<>();

	/**
	 * Method used to get back the {@link XALDocument}
	 * @return Document that represent the IM in the XAL format
	 */
	public XALDocument getXalDocument() {
		return xalDocument;
	}

	public GenerateXAL(ASTClass elm) {
		xalDocument = new XALDocument(elm.getName());
		convert(elm);
	}

	private void convert(ASTClass elm){
		for(ASTStatic s : elm.getStaticInit()){
			convertStatic(s);
		}
		for(IASTMethod m : elm.getMethods()){
			if(m instanceof ASTMethod)
				convertMethod((ASTMethod) m);
			else
				convertConstrucutor((ASTConstructor) m);
		}
	}

	private void convertStatic(ASTStatic s){

	}

	private void convertMethod(ASTMethod elm){
		XALAutomaton xalAutomaton = new XALAutomaton(elm.getName());
		for(ASTVariable v : elm.getParameters()) {
			xalAutomaton.getGlobalState().addVariable(
					new XALVariable(v.getName(), v.getType(), "", XALVariable.XALIO.I)
			);
		}
		XALState init = new XALState("init");
		xalAutomaton.addState(init);
		try {
			xalAutomaton.setInitialState(init);
		} catch (XALMalformedException e) {
			e.printStackTrace();
		}
		lastAutomatonWhereAdd = xalAutomaton;
		if(elm.isSynchronized()){
			XALSync sync = new XALSync(elm.getName(), lastAutomatonWhereAdd);
			xalAutomaton.addState(sync);
			lastAutomatonWhereAdd = sync;
		}
		xalDocument.addAutomaton(xalAutomaton);
		lastState = init;
		lastAutomaton = xalAutomaton;
		for(IASTStm s : elm.getStms()){
			dispachStm(s);
		}
	}

	private void convertConstrucutor(ASTConstructor elm){
		XALAutomaton xalAutomaton = new XALAutomaton(elm.getName());
		for(ASTVariable v : elm.getParameters()) {
			xalAutomaton.getGlobalState().addVariable(
					new XALVariable(v.getName(), v.getType(), "", XALVariable.XALIO.I)
			);
		}
		XALState init = new XALState("init");
		xalAutomaton.addState(init);
		try {
			xalAutomaton.setInitialState(init);
		} catch (XALMalformedException e) {
			e.printStackTrace();
		}
		xalDocument.addAutomaton(xalAutomaton);
		lastState = init;
		lastAutomaton = xalAutomaton;
		lastAutomatonWhereAdd = xalAutomaton;
		for(IASTStm s : elm.getStms()){
			dispachStm(s);
		}
	}

	private void convertRE(ASTRE elm) {
		XALState s = new XALState(elm.getExpressionName());
		addState(s, elm);
	}
	private void convertBreak(ASTBreak elm) {
		XALState s = new XALState("break");
		addState(s, elm);
	}
	private void convertContinue(ASTContinue elm) {
		XALState s = new XALState("continue");
		addState(s, elm);
	}

	private void convertReturn(ASTReturn elm) {
		XALState s = new XALState("return" +
				(elm.getExpr() != null ?  elm.getExpr().getExpressionName() : "")
		);
		addState(s, elm);
		lastAutomaton.addFinalState(s);
	}


	private void convertThrow(ASTThrow elm){
		XALState s = new XALState("throw" + elm.getExpr().getExpressionName());
		addState(s, elm);
		lastAutomaton.addFinalState(s);
	}

	private void convertIf(ASTIf elm) {

		convertRE(elm.getGuard());
		XALState _if = lastState;
		metricValue = XALTransition.METRIC_TRUE;
		for(IASTStm s : elm.getIfBranch().getStms()){
			dispachStm(s);
		}
		XALState _end = new XALState("endif");
		lastAutomatonWhereAdd.addState(_end);
		XALTransition teif = new XALTransition(lastState,_end);
		lastAutomaton.addTransition(teif);
		if(elm.getElseBranch() != null){
			lastState = _if;
			metricValue = XALTransition.METRIC_FALSE;
			for(IASTStm s : elm.getElseBranch().getStms()){
				dispachStm(s);
			}
			XALTransition te = new XALTransition(lastState,_end);
			lastAutomaton.addTransition(te);
		} else {
			XALTransition te = new XALTransition(_if,_end, XALTransition.METRIC_FALSE);
			lastAutomaton.addTransition(te);
		}

		lastState = _end;
	}

	private void convertDoWhile(ASTDoWhile elm){
		XALState _do = new XALState("do");
		XALState _enddo = new XALState("enddo");
		lastAutomatonWhereAdd.addState(_do);
		lastAutomatonWhereAdd.addState(_enddo);
		XALTransition tinit = new XALTransition(lastState,_do);
		lastAutomaton.addTransition(tinit);
		lastState = _do;
		//do the stms
		for(IASTStm s : elm.getStms()){
			dispachStm(s);
		}
		//expr
		convertRE(elm.getExpr());
		XALTransition texpr = new XALTransition(lastState,_do, XALTransition.METRIC_TRUE);
		lastAutomaton.addTransition(texpr);
		XALTransition tend = new XALTransition(lastState, _enddo, XALTransition.METRIC_FALSE);
		lastAutomaton.addTransition(tend);
		lastState = _enddo;
	}

	private void convertFor(ASTFor elm) {
		XALState _for_init = new XALState("for_init");
		XALState _for_guard = new XALState("for_guard");
		XALState _for_post = new XALState("for_post");
		XALState _for_end = new XALState("for_end");

		lastAutomatonWhereAdd.addState(_for_init);
		lastAutomatonWhereAdd.addState(_for_guard);
		lastAutomatonWhereAdd.addState(_for_post);
		lastAutomatonWhereAdd.addState(_for_end);

		//init express
		XALTransition tinit = new XALTransition(lastState,_for_init);
		lastAutomaton.addTransition(tinit);
		lastState = _for_init;
		for(ASTRE e : elm.getInit()){
			convertRE(e);
		}

		//guard
		XALTransition tGuard = new XALTransition(lastState,_for_guard);
		lastAutomaton.addTransition(tGuard);
		lastState = _for_guard;
		convertRE(elm.getExpr());

		//if false go to end of the for
		XALTransition tEnd = new XALTransition(lastState, _for_end, XALTransition.METRIC_FALSE);
		lastAutomaton.addTransition(tEnd);

		//stms
		metricValue = XALTransition.METRIC_TRUE;
		for(IASTStm s : elm.getStms()){
			dispachStm(s);
		}

		//post
		XALTransition tPost = new XALTransition(lastState,_for_post);
		lastAutomaton.addTransition(tPost);
		lastState = _for_post;
		for(ASTRE e : elm.getPost()){
			convertRE(e);
		}

		//back to the guars
		XALTransition tBack = new XALTransition(lastState,_for_guard);
		lastAutomaton.addTransition(tBack);

		lastState = _for_end;

	}


	private void convertForeach(ASTForEach elm){
		XALState _for_guard = new XALState("hasNext");
		XALState _for_end = new XALState("for_end");
		lastAutomatonWhereAdd.addState(_for_end);
		lastAutomatonWhereAdd.addState(_for_guard);

		//init to the guars
		XALTransition tInit = new XALTransition(lastState,_for_guard);
		lastAutomaton.addTransition(tInit);

		//if no next
		XALTransition tEnd = new XALTransition(_for_guard, _for_end, XALTransition.METRIC_FALSE);
		lastAutomaton.addTransition(tEnd);


		//has next
		XALState guard = new XALState( elm.getVar().getName() + "_takes_" + elm.getExpr().getExpressionName() );
		lastAutomatonWhereAdd.addState(guard);
		XALTransition tGuard = new XALTransition(_for_guard, guard, XALTransition.METRIC_TRUE);
		lastAutomaton.addTransition(tGuard);
		lastState = guard;

		for(IASTStm s : elm.getStms()){
			dispachStm(s);
		}

		//back to the guars
		XALTransition tBack = new XALTransition(lastState,_for_guard);
		lastAutomaton.addTransition(tBack);

		lastState = _for_end;
	}

	private void convertASTSwitch(ASTSwitch elm){
		XALState _enter = new XALState("switch");
		XALState _exit = new XALState("endSwitch");
		lastAutomatonWhereAdd.addState(_enter);
		lastAutomatonWhereAdd.addState(_exit);

		XALTransition tInit = new XALTransition(lastState, _enter);
		lastAutomaton.addTransition(tInit);

		//expr
		XALState check = new XALState("check_" + elm.getExpr().getExpressionName());
		lastAutomatonWhereAdd.addState(check);
		XALTransition tCheck = new XALTransition(_enter, check);
		lastAutomaton.addTransition(tCheck);
		lastState = check;

		for(ASTSwitch.ASTCase c : elm.getCases()){

			for(String label : c.getLabels()){
				XALState _case = new XALState("equals_" + label);
				lastAutomatonWhereAdd.addState(_case);
				switchLabels.add(_case);
				XALTransition tCase = new XALTransition(check, _case);
				lastAutomaton.addTransition(tCase);
			}
			isSwitch = true;
			for(IASTStm s : c.getStms()){
				dispachStm(s);
			}
			if(c.getStms().size() > 0) {
				switchLabels.clear();
				XALTransition tEnd = new XALTransition(lastState, _exit);
				lastAutomaton.addTransition(tEnd);
			}

		}
		lastState = _exit;
	}

	private void convertSyncronized(ASTSynchronized elm){
		XALAddState bck = lastAutomatonWhereAdd;
		XALSync s = new XALSync(elm.getExpr().getExpressionName(), bck);
		lastAutomatonWhereAdd.addState(s);
		lastAutomatonWhereAdd = s;

		for(IASTStm stm : elm.getStms()){
			dispachStm(stm);
		}

		lastAutomatonWhereAdd = bck;
	}

	private void convertTryResource(ASTTryResources elm){
		XALState _enter = new XALState("try");
		XALState _finally = new XALState("finally");
		XALState _exit = new XALState("endtry");

		lastAutomatonWhereAdd.addState(_enter);
		lastAutomatonWhereAdd.addState(_finally);
		lastAutomatonWhereAdd.addState(_exit);

		XALTransition tInit = new XALTransition(lastState, _enter);
		lastAutomaton.addTransition(tInit);
		lastState = _enter;
		for(IASTStm stm :elm.getTryBranch().getStms()){
			dispachStm(stm);
		}

		XALTransition tFinally = new XALTransition(lastState, _finally);
		lastAutomaton.addTransition(tFinally);
		lastState = _finally;
		if(elm.getFinallyBranch() != null) {
			for (IASTStm stm : elm.getFinallyBranch().getStms()) {
				dispachStm(stm);
			}
		}
		//release of resources
		for(ASTRE r : elm.getResources()){
			XALState s = new XALState("close_" + r.getExpressionName());
			addState(s, r);
		}
		XALTransition tEnd = new XALTransition(lastState, _exit);
		lastAutomaton.addTransition(tEnd);

		//catch?
		if(elm.getCatchBranch().size() > 0){
			XALState _catch = new XALState("catch");
			XALTransition tCatch = new XALTransition(_enter, _catch);
			lastAutomatonWhereAdd.addState(_catch);
			lastAutomaton.addTransition(tCatch);
			for(ASTTry.ASTCatchBranch c : elm.getCatchBranch()){
				lastState = _catch;
				XALState s = new XALState(c.getExpr().getName() + "_instanceOf_" + c.getExpr().getType());
				addState(s, c);
				lastState = s;
				metricValue = XALTransition.METRIC_TRUE;
				for(IASTStm stm : c.getStms()){
					dispachStm(stm);
				}
				XALTransition tClose = new XALTransition(lastState, _exit);
				lastAutomaton.addTransition(tClose);
			}
		}
		lastState = _exit;
	}

	private void convertTry(ASTTry elm){
		XALState _enter = new XALState("try");
		XALState _finally = new XALState("finally");
		XALState _exit = new XALState("endtry");

		lastAutomatonWhereAdd.addState(_enter);
		lastAutomatonWhereAdd.addState(_finally);
		lastAutomatonWhereAdd.addState(_exit);

		XALTransition tInit = new XALTransition(lastState, _enter);
		lastAutomaton.addTransition(tInit);
		lastState = _enter;
		for(IASTStm stm :elm.getTryBranch().getStms()){
			dispachStm(stm);
		}

		XALTransition tFinally = new XALTransition(lastState, _finally);
		lastAutomaton.addTransition(tFinally);
		lastState = _finally;
		if(elm.getFinallyBranch() != null) {
			for (IASTStm stm : elm.getFinallyBranch().getStms()) {
				dispachStm(stm);
			}
		}

		XALTransition tEnd = new XALTransition(lastState, _exit);
		lastAutomaton.addTransition(tEnd);

		//catch?
		if(elm.getCatchBranch().size() > 0){
			XALState _catch = new XALState("catch");
			XALTransition tCatch = new XALTransition(_enter, _catch);
			lastAutomatonWhereAdd.addState(_catch);
			lastAutomaton.addTransition(tCatch);
			for(ASTTry.ASTCatchBranch c : elm.getCatchBranch()){
				lastState = _catch;
				XALState s = new XALState(c.getExpr().getName() + "_instanceOf_" + c.getExpr().getType());
				addState(s, c);
				lastState = s;
				metricValue = XALTransition.METRIC_TRUE;
				for(IASTStm stm : c.getStms()){
					dispachStm(stm);
				}
				XALTransition tClose = new XALTransition(lastState, _exit);
				lastAutomaton.addTransition(tClose);
			}
		}
		lastState = _exit;
	}

	private void convertWhile(ASTWhile elm){
		XALState _enter = new XALState("while");
		XALState _exit = new XALState("endWhile");
		lastAutomatonWhereAdd.addState(_enter);
		lastAutomatonWhereAdd.addState(_exit);


		XALTransition tInit = new XALTransition(lastState, _enter);
		lastAutomaton.addTransition(tInit);

		lastState = _enter;
		//check
		convertRE(elm.getExpr());
		XALState guard = lastState;
		//go to end
		XALTransition tClose = new XALTransition(lastState, _exit, XALTransition.METRIC_FALSE);
		lastAutomaton.addTransition(tClose);

		metricValue = XALTransition.METRIC_TRUE;
		for(IASTStm stm : elm.getStms()){
			dispachStm(stm);
		}

		XALTransition tGuard = new XALTransition(lastState, guard);
		lastAutomaton.addTransition(tGuard);
		lastState = _exit;
	}


	/**
	 * Ugly workaround to have pattern matching in haskel fashion on java
	 * @param stm
	 */
	private void dispachStm(IASTStm stm){
		if (stm instanceof ASTRE){
			convertRE((ASTRE) stm);
		}
		else if(stm	instanceof ASTBreak){
			convertBreak((ASTBreak)stm);
		}
		else if(stm	instanceof ASTContinue){
			convertContinue((ASTContinue) stm);
		}
		else if(stm	instanceof ASTDoWhile){
			convertDoWhile((ASTDoWhile)stm);
		}
		else if(stm	instanceof ASTFor){
			convertFor((ASTFor)stm);
		}
		else if(stm	instanceof ASTForEach){
			convertForeach((ASTForEach)stm);
		}
		else if(stm	instanceof ASTIf){
			convertIf((ASTIf)stm);
		}
		else if(stm	instanceof ASTReturn){
			convertReturn((ASTReturn)stm);
		}
		else if(stm	instanceof ASTSwitch){
			convertASTSwitch((ASTSwitch)stm);
		}
		else if(stm	instanceof ASTSynchronized){
			convertSyncronized((ASTSynchronized)stm);
		}
		else if(stm	instanceof ASTThrow){
			convertThrow((ASTThrow)stm);
		}
		//the order is important because ASTTry is extended by ASTTryResources
		else if(stm	instanceof ASTTryResources){
			convertTryResource((ASTTryResources)stm);
		}
		else if(stm	instanceof ASTTry){
			convertTry((ASTTry)stm);
		}
		else if(stm	instanceof ASTWhile){
			convertWhile((ASTWhile)stm);
		}
	}

	public void addState(XALState state, IASTStm stm){

		lastAutomatonWhereAdd.addState(state);
		XALTransition transition = null;
		if(isSwitch){
			isSwitch = false;
			for(XALState last : switchLabels){
				transition = new XALTransition(last, state);
				lastAutomaton.addTransition(transition);
			}
		} else {
			transition = new XALTransition(this.lastState, state, this.metricValue);
			lastAutomaton.addTransition(transition);
		}
		if(stm.isTimeCritical() && stm.getConstraint() != null){
			String constraint = "<ClockConstraint ClockExp=\"" + stm.getConstraint().getValue() +"\"/>";
			state.setTimeConstraint(constraint);
		}
		this.lastState = state;
		this.metricValue = "";
	}
}
