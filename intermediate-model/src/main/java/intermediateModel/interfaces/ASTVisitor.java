package intermediateModel.interfaces;


import intermediateModel.structure.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface ASTVisitor extends ASTREVisitor {
	void enterASTAttribute(ASTAttribute elm);
	void enterASTBreak(ASTBreak elm);
	void enterASTClass(ASTClass elm);
	void enterASTConstructor(ASTConstructor elm);
	void enterASTContinue(ASTContinue elm);
	void enterASTDoWhile(ASTDoWhile elm);
	void enterASTFor(ASTFor elm);
	void enterASTForEach(ASTForEach elm);
	void enterASTIf(ASTIf elm);
	void enterASTImport(ASTImport elm);
	void enterASTMethod(ASTMethod elm);
	void enterASTRE(ASTRE elm);
	void enterASTReturn(ASTReturn elm);
	void enterASTStatic(ASTStatic elm);
	void enterASTSwitch(ASTSwitch elm);
	void enterASTSynchronized(ASTSynchronized elm);
	void enterASTThrow(ASTThrow elm);
	void enterASTTry(ASTTry elm);
	void enterASTTryResources(ASTTryResources elm);
	void enterASTVariable(ASTVariable elm);
	void enterASTWhile(ASTWhile elm);
	void enterASTHiddenClass(ASTHiddenClass astHiddenClass);
	void enterSTM(IASTStm s);

	void exitASTAttribute(ASTAttribute elm);
	void exitASTBreak(ASTBreak elm);
	void exitASTClass(ASTClass elm);
	void exitASTConstructor(ASTConstructor elm);
	void exitASTContinue(ASTContinue elm);
	void exitASTDoWhile(ASTDoWhile elm);
	void exitASTFor(ASTFor elm);
	void exitASTForEach(ASTForEach elm);
	void exitASTIf(ASTIf elm);
	void exitASTImport(ASTImport elm);
	void exitASTMethod(ASTMethod elm);
	void exitASTRE(ASTRE elm);
	void exitASTReturn(ASTReturn elm);
	void exitASTStatic(ASTStatic elm);
	void exitASTSwitch(ASTSwitch elm);
	void exitASTSynchronized(ASTSynchronized elm);
	void exitASTThrow(ASTThrow elm);
	void exitASTTry(ASTTry elm);
	void exitASTTryResources(ASTTryResources elm);
	void exitASTVariable(ASTVariable elm);
	void exitASTWhile(ASTWhile elm);
	void exitASTHiddenClass(ASTHiddenClass astHiddenClass);
	void exitSTM(IASTStm s);
}
