package intermediateModel.interfaces;


import intermediateModel.structure.expression.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface ASTREVisitor {
	void enterASTArrayInitializer(ASTArrayInitializer elm);
	void enterASTAssignment(ASTAssignment elm);
	void enterASTAttributeAccess(ASTAttributeAccess elm);
	void enterASTbinary(ASTBinary elm);
	void enterASTCast(ASTCast elm);
	void enterASTConditional(ASTConditional elm);
	void enterASTLiteral(ASTLiteral elm);
	void enterASTMethodCall(ASTMethodCall elm);
	void enterASTNewObject(ASTNewObject elm);
	void enterASTPostOp(ASTPostOp elm);
	void enterASTPreOp(ASTPreOp elm);
	void enterASTUnary(ASTUnary elm);
	void enterASTVariableDeclaration(ASTVariableDeclaration elm);
	void enterASTVariableMultipleDeclaration(ASTVariableMultipleDeclaration elm);
	void enterNotYetImplemented(NotYetImplemented elm);
	void enterAll(IASTRE elm);

	void exitASTArrayInitializer(ASTArrayInitializer elm);
	void exitASTAssignment(ASTAssignment elm);
	void exitASTAttributeAccess(ASTAttributeAccess elm);
	void exitASTbinary(ASTBinary elm);
	void exitASTCast(ASTCast elm);
	void exitASTConditional(ASTConditional elm);
	void exitASTLiteral(ASTLiteral elm);
	void exitASTMethodCall(ASTMethodCall elm);
	void exitASTNewObject(ASTNewObject elm);
	void exitASTPostOp(ASTPostOp elm);
	void exitASTPreOp(ASTPreOp elm);
	void exitASTUnary(ASTUnary elm);
	void exitASTVariableDeclaration(ASTVariableDeclaration elm);
	void exitASTVariableMultipleDeclaration(ASTVariableMultipleDeclaration elm);
	void exitNotYetImplemented(NotYetImplemented elm);
	void exitAll(IASTRE elm);


	boolean isExcludePars();
	boolean isExcludeHiddenClasses();

}
