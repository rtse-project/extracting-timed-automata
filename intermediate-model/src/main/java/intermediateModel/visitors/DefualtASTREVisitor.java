package intermediateModel.visitors;

import intermediateModel.interfaces.ASTREVisitor;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.expression.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class DefualtASTREVisitor implements ASTREVisitor {

	boolean excludePars = false;
	boolean excludeHiddenClass = false;

	public void setExcludePars(boolean excludePars) {
		this.excludePars = excludePars;
	}

	public void setExcludeHiddenClass(boolean excludeHiddenClass) {
		this.excludeHiddenClass = excludeHiddenClass;
	}

	public DefualtASTREVisitor setExcludeHiddenClassContinuos(boolean excludeHiddenClass) {
		this.excludeHiddenClass = excludeHiddenClass;
		return this;
	}

	public DefualtASTREVisitor setExcludeParsContinuos(boolean excludePars) {
		this.excludePars = excludePars;
		return this;
	}

	public boolean isExcludePars() {
		return excludePars;
	}

	@Override
	public boolean isExcludeHiddenClasses() {
		return excludeHiddenClass;
	}

	@Override
	public void enterASTArrayInitializer(ASTArrayInitializer elm) {

	}

	@Override
	public void enterASTAssignment(ASTAssignment elm) {

	}

	@Override
	public void enterASTAttributeAccess(ASTAttributeAccess elm) {

	}

	@Override
	public void enterASTbinary(ASTBinary elm) {

	}

	@Override
	public void enterASTCast(ASTCast elm) {

	}

	@Override
	public void enterASTConditional(ASTConditional elm) {

	}

	@Override
	public void enterASTLiteral(ASTLiteral elm) {

	}

	@Override
	public void enterASTMethodCall(ASTMethodCall elm) {

	}

	@Override
	public void enterASTNewObject(ASTNewObject elm) {

	}

	@Override
	public void enterASTPostOp(ASTPostOp elm) {

	}

	@Override
	public void enterASTPreOp(ASTPreOp elm) {

	}

	@Override
	public void enterASTUnary(ASTUnary elm) {

	}

	@Override
	public void enterASTVariableDeclaration(ASTVariableDeclaration elm) {

	}

	@Override
	public void enterASTVariableMultipleDeclaration(ASTVariableMultipleDeclaration elm) {

	}

	@Override
	public void enterNotYetImplemented(NotYetImplemented elm) {

	}

	@Override
	public void enterAll(IASTRE elm) {

	}

	@Override
	public void exitASTArrayInitializer(ASTArrayInitializer elm) {

	}

	@Override
	public void exitASTAssignment(ASTAssignment elm) {

	}

	@Override
	public void exitASTAttributeAccess(ASTAttributeAccess elm) {

	}

	@Override
	public void exitASTbinary(ASTBinary elm) {

	}

	@Override
	public void exitASTCast(ASTCast elm) {

	}

	@Override
	public void exitASTConditional(ASTConditional elm) {

	}

	@Override
	public void exitASTLiteral(ASTLiteral elm) {

	}

	@Override
	public void exitASTMethodCall(ASTMethodCall elm) {

	}

	@Override
	public void exitASTNewObject(ASTNewObject elm) {

	}

	@Override
	public void exitASTPostOp(ASTPostOp elm) {

	}

	@Override
	public void exitASTPreOp(ASTPreOp elm) {

	}

	@Override
	public void exitASTUnary(ASTUnary elm) {

	}

	@Override
	public void exitASTVariableDeclaration(ASTVariableDeclaration elm) {

	}

	@Override
	public void exitASTVariableMultipleDeclaration(ASTVariableMultipleDeclaration elm) {

	}

	@Override
	public void exitNotYetImplemented(NotYetImplemented elm) {

	}

	@Override
	public void exitAll(IASTRE elm) {

	}
}
