package intermediateModel.visitors.interfaces;

import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.expression.*;
import intermediateModelHelper.envirorment.Env;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class ParseRE {
	protected Env e;
	protected ParseRE(Env e){
		this.e = e;
	}

	protected Object analyze(IASTRE r){
		if(r instanceof ASTArrayInitializer){
			return analyze((ASTArrayInitializer) r);
		} else if(r instanceof ASTAssignment) {
			return analyze((ASTAssignment) r);
		} else if(r instanceof ASTAttributeAccess) {
			return analyze((ASTAttributeAccess) r);
		} else if(r instanceof ASTBinary) {
			return analyze((ASTBinary) r);
		} else if(r instanceof ASTCast) {
			return analyze((ASTCast) r);
		} else if(r instanceof ASTConditional){
			return analyze((ASTConditional) r);
		} else if(r instanceof ASTLiteral){
			return analyze((ASTLiteral) r);
		} else if(r instanceof ASTMethodCall){
			return analyze((ASTMethodCall) r);
		} else if(r instanceof ASTNewObject) {
			return analyze((ASTNewObject) r);
		} else if(r instanceof ASTPostOp) {
			return analyze((ASTPostOp) r);
		} else if(r instanceof ASTPreOp) {
			return analyze((ASTPreOp) r);
		} else if(r instanceof ASTUnary) {
			return analyze((ASTUnary) r);
		} else if(r instanceof ASTVariableDeclaration) {
			return analyze((ASTVariableDeclaration) r);
		} else if(r instanceof ASTVariableMultipleDeclaration) {
			return analyze((ASTVariableMultipleDeclaration) r);
		} else {
			return null;
		}
	}

	protected Object analyze(ASTArrayInitializer r){ return null; }
	protected Object analyze(ASTAssignment r){ return null; }
	protected Object analyze(ASTAttributeAccess r){ return null; }
	protected Object analyze(ASTBinary r){ return null; }
	protected Object analyze(ASTCast r){ return null; }
	protected Object analyze(ASTConditional r){ return null; }
	protected Object analyze(ASTLiteral r){ return null; }
	protected Object analyze(ASTMethodCall r){ return null; }
	protected Object analyze(ASTNewObject r){ return null; }
	protected Object analyze(ASTPostOp r){ return null; }
	protected Object analyze(ASTPreOp r){ return null; }
	protected Object analyze(ASTUnary r){ return null; }
	protected Object analyze(ASTVariableDeclaration r){ return null; }
	protected Object analyze(ASTVariableMultipleDeclaration r){ return null; }
	

}
