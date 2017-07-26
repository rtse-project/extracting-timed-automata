package intermediateModel.visitors.interfaces;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.*;
import intermediateModel.structure.expression.ASTNewObject;

import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class ConvertIM {

	public void processClass(ASTClass c){
		for(IASTMethod m : c.getMethods()){
			dispatchMethod(m, c);
		}
	}

	protected void dispatchMethod(IASTMethod m, ASTClass c){
		dispachStm(m.getStms());
		if(m instanceof ASTConstructor){
			for(ASTStatic init : c.getStaticInit()){
				dispachStm(init.getStms());
			}
		}
	}

	protected void dispachStm(List<IASTStm> stms){
		for(IASTStm stm : stms){
			dispachStm(stm);
		}
	}
	/**
	 * Ugly workaround to have pattern matching in haskel fashion on java
	 * @param stm	Statement to analyze
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
			convertSynchronized((ASTSynchronized)stm);
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
		else if(stm instanceof ASTNewObject){
			convertASTNewObject((ASTNewObject) stm);
		}
		else if(stm instanceof ASTHiddenClass){
			convertASTHiddenClass((ASTHiddenClass)stm);
		}
	}


	protected abstract void convertASTHiddenClass(ASTHiddenClass stm);
	protected abstract void convertASTNewObject(ASTNewObject stm);
	protected abstract void convertWhile(ASTWhile stm);
	protected abstract void convertTry(ASTTry stm);
	protected abstract void convertTryResource(ASTTryResources stm);
	protected abstract void convertThrow(ASTThrow stm);
	protected abstract void convertSynchronized(ASTSynchronized stm);
	protected abstract void convertASTSwitch(ASTSwitch stm) ;
	protected abstract void convertReturn(ASTReturn stm);
	protected abstract void convertIf(ASTIf stm);
	protected abstract void convertForeach(ASTForEach stm);
	protected abstract void convertFor(ASTFor stm);
	protected abstract void convertDoWhile(ASTDoWhile stm) ;
	protected abstract void convertContinue(ASTContinue stm);
	protected abstract void convertBreak(ASTBreak stm);
	protected abstract void convertRE(ASTRE stm);

}
