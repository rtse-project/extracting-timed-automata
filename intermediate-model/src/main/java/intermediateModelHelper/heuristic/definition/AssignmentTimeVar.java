package intermediateModelHelper.heuristic.definition;


import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTConstructor;
import intermediateModel.structure.ASTMethod;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTAssignment;
import intermediateModel.structure.expression.ASTLiteral;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.structure.expression.ASTVariableDeclaration;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.CheckExpression;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeTypes;

import java.util.List;

/**
 * The server.heuristic searches for snippet of code in a guard section of the following type:
 * <pre>
 * {@code  var - x < y | var < y }
 * </pre>
 * Where var is a variable that has a type time related. Regarding the x and y, they can either be constant
 * values or be time related variables.
 * The search is not limited to the <i>&lt;</i> operator, it searches for all the order operators.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 */
public class AssignmentTimeVar extends SearchTimeConstraint {

	IASTMethod currentMethod;

	@Override
	public void setup(ASTClass c) {
		super.setup(c);
	}

	@Override
	public void nextMethod(ASTMethod method, Env env) {
		super.nextMethod(method, env);
		currentMethod = method;
	}

	@Override
	public void nextConstructor(ASTConstructor method, Env env) {
		super.nextConstructor(method, env);
		currentMethod = method;
	}

	@Override
	public void next(ASTRE stm, Env env) {
		//works only on ASTRE
		IASTRE expr = stm.getExpression();
		if(expr == null){
			return;
		}


		if(CheckExpression.checkRE(stm,env)){
			stm.markResetTime();
		}

		//record time vars
		stm.visit(new DefaultASTVisitor(){
			@Override
			public void enterASTLiteral(ASTLiteral elm) {
				String name = elm.getValue();
				if(env.existVarNameTimeRelevant(name)){
					AssignmentTimeVar.super.addTimeVar(currentMethod, name);
				}
			}
		});

		//search for assignment of time values
		expr.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTAssignment(ASTAssignment elm) {
				IASTRE l = elm.getLeft();
				if(l instanceof ASTLiteral){
					String varName = ((ASTLiteral) l).getValue();
					IASTVar v = env.getVar(varName);
					if(v != null && v.isTimeCritical()){
						addExpression(stm, elm);
						addConstraint(elm);
					}
				}
			}

			@Override
			public void enterASTVariableDeclaration(ASTVariableDeclaration elm) {
				String varName = elm.getNameString();
				if(env.existVarNameTimeRelevant(varName)){
					addExpression(stm, elm);
					addConstraint(elm);
				}
			}
		});

	}



	private void addExpression(ASTRE stm, ASTAssignment assignment) {
		searchResetTime(assignment.getRight());
		stm.setResetExpression(assignment.getRight().print());
	}

	private void addExpression(ASTRE stm, ASTVariableDeclaration varDec) {
		searchResetTime(varDec.getExpr());
		stm.setResetExpression(varDec.getExpr().print());
	}

	private void searchResetTime(IASTRE expr) {
		List<TimeTypes> l = TemporalInfo.getInstance().getTimeTypes();
		expr.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				String pointer = elm.getClassPointed();
				String name = elm.getMethodName();
				List<IASTRE> pars = elm.getParameters();
				int size = pars.size();
				if(pointer != null && containTimeOut(pointer, name, size, l)) {
					elm.setTimeCall(true);
				}
			}
		});
	}

	private boolean containTimeOut(String pointer, String name, int size, List<TimeTypes> l) {
		for(TimeTypes m : l){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == size)
				return true;
		}
		return false;
	}

	protected void addConstraint(ASTAssignment stm) {
		super.addConstraint(stm.print(), stm, true);
	}

	private void addConstraint(ASTVariableDeclaration stm) {
		super.addConstraint(stm.print(), stm, true);
	}

}
