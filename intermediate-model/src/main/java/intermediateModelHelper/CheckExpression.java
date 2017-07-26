package intermediateModelHelper;

import intermediateModel.interfaces.IASTRE;
import intermediateModel.interfaces.IASTVar;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.ASTVariable;
import intermediateModel.structure.expression.*;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.BuildEnvironment;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.TimeMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class CheckExpression {

	static List<TimeMethod>  timeMethods = TemporalInfo.getInstance().getMethodsWithTimeInSignature();

	/**
	 * This method add to the definition of variable of the Env passed as parameter all the time relevant information.
	 * @param lPars List of parameters to check
	 * @param env {@link Env} class where to add the new definition
	 * @return The envirorment (maybe) enriched.
	 */
	public static Env checkPars(List<ASTVariable> lPars, Env env){
		for(ASTVariable v : lPars){
			setVariableInEnv(v, env);
		}
		return env;
	}

	/**
	 * This method will go through the definition of a RExpr and check for the following:
	 * <ul>
	 *     <li>Definition of new vars</li>
	 *     <li>Assignment of a value to a var/li>
	 * </ul>
	 * If something is time related, it will be added to the Env that is returned.
	 * @param rexp {@link ASTRE} to check
	 * @param env {@link Env} class where to add the new definition
	 *
	 */
	public static boolean checkRE(ASTRE rexp, Env env){
		if(rexp == null){
			return false;
		}

		final boolean[] flag = {false};
		rexp.getExpression().visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTVariableDeclaration(ASTVariableDeclaration elm) {
				flag[0] = flag[0] || setVariableInEnv(rexp, elm, env);
			}

			@Override
			public void enterASTAssignment(ASTAssignment elm) {
				flag[0] = flag[0] || setVariableInEnv(rexp, elm, env);
			}


		});

		return flag[0];
	}

	public static boolean checkMethodCall(ASTRE rexp, Env env){

		if(rexp == null){
			return false;
		}
		final boolean[] flag = {false};
		rexp.getExpression().visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				flag[0] = flag[0] || setMethodCall(elm, env);
			}

		});
		return flag[0];

	}

	private static boolean setMethodCall(ASTMethodCall elm, Env env) {
		String pointer = elm.getClassPointed();
		String name = elm.getMethodName();
		List<IASTRE> pars = elm.getParameters();
		int size = pars.size();
		boolean flag = false;
		if(pointer != null && containTimeOut(pointer, name, size)) {
			flag = true;
			elm.setTimeCall(true);
			String timeout = "";
			TimeMethod m = getTimeOut(pointer,name, size);
			int[] p = m.getTimeouts();
			for(int i : p){
				IASTRE r = pars.get(i);
				r.visit(new DefualtASTREVisitor(){
					@Override
					public void enterASTLiteral(ASTLiteral elm) {
						IASTVar v = env.getVar(elm.getValue());
						if(v != null){
							v.setTimeCritical(true);
						}
					}
				});
			}
		}
		return flag;
	}

	private static boolean containTimeOut(String pointer, String name, int nPars){
		for(TimeMethod m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
	}

	private static TimeMethod getTimeOut(String pointer, String name, int nPars){
		for(TimeMethod m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return m;
		}
		return null;
	}

	/**
	 * The method accept a {@link ASTVariable} and an {@link Env} and checks if is time
	 * relevant. In the positive case, it adds to the envirorment the variable.
	 * @param v			Variable to control
	 * @param where		Envirorment where to add
	 */
	public static boolean setVariableInEnv(ASTVariable v, Env where){
		//boolean flag = BuildEnvironment.getInstance().hasVarTypeTimeRelated(v);
		//v.setTimeCritical( flag );
		where.addVar(v);
		return v.isTimeCritical();
	}

	/**
	 * The method accept a {@link ASTVariableDeclaration} and an {@link Env}.
	 * It checks if the varible is a time relevant type and in the positive case it adds the var to the envirorment.
	 * Moreover, it checks if the expression of inizialization is a time relevant expression.
	 * The checks that the method performs are regarding to:
	 * <ul>
	 *     <li>There is a call to a method that is time relevant</li>
	 *     <li>There is a math expression that involves time relevant variable</li>
	 * </ul>
	 * @param v			Variable to check
	 * @param where		Envirorment where to add
	 */
	private static boolean setVariableInEnv(ASTRE state, ASTVariableDeclaration v, Env where){
		//check the type
		ASTVariable var = new ASTVariable(v.getStart(),v.getEnd(), v.getNameString(), v.getType());
		var.setTypePointed(v.getTypePointed());
		var.setSouceCode(v.getCode());
		var.setTimeCritical(v.isTimeCritical());
		setVariableInEnv(var, where);
		//check the expr
		boolean flag = checkRightHandAssignment(state, v.getExpr(), where);
		if(flag){
			v.getExpr().setTimeCritical(true);
			v.setTimeCritical(true);
			var.setTimeCritical(true);
			where.addVar(var);
		}
		return flag;
	}

	/**
	 * The method accept a {@link ASTVariableDeclaration} and an {@link Env}.
	 * It checks if the varible is a time relevant type and in the positive case it adds the var to the envirorment.
	 * Moreover, it checks if the expression of inizialization is a time relevant expression.
	 * The checks that the method performs are regarding to:
	 * <ul>
	 *     <li>There is a call to a method that is time relevant</li>
	 *     <li>There is a math expression that involves time relevant variable</li>
	 * </ul>
	 * @param v			Variable to check
	 * @param where		Envirorment where to add
	 */
	private static boolean setVariableInEnv(ASTRE state, ASTAssignment v, Env where){
		IASTRE left = v.getLeft();
		final boolean[] flag = new boolean[1];
		if(left instanceof ASTLiteral){
			String name = ((ASTLiteral) left).getValue();
			IASTVar var = where.getVar(name);
			if(var != null //should be never the case if code compiles
					&& checkRightHandAssignment(state, v.getRight(), where)){ //if exists something time related
				var.setTimeCritical(true);
				v.getRight().setTimeCritical(true);
				flag[0] = true;//the assigned var is time relevant
				setExprVarsTimeRelated(v.getRight(), where);
			}
		}
		return flag[0];
	}

	public static boolean checkRightHandAssignment(ASTRE state, IASTRE expr, Env where){
		final boolean[] flag = {false};
		if(expr != null) {
			DefualtASTREVisitor visit = new DefualtASTREVisitor() {

				//method call
				@Override
				public void enterASTMethodCall(ASTMethodCall elm) {
					if(isValidType(state, elm)){
						if(elm.getClassPointed() != null && !elm.getClassPointed().equals("")){
							if(where.existMethodTimeRelevant(elm.getClassPointed(), elm.getMethodName(), getSignature(elm.getParameters(), where))){
								flag[0] = true;
							} else if (elm.getClassPointed().equals("java.lang.Math")){
								String name = elm.getMethodName();
								if(name.equals("min") || name.equals("max")){
									if(checkMinMaxTime(elm, where, state)){
										flag[0] = true;
									}
								}
							}
						}
						else if(where.existMethodTimeRelevant( elm.getMethodName(), getSignature(elm.getParameters(), where) )){
							flag[0] = true;
						}
					}
				}

				//math op between time
				@Override
				public void enterASTbinary(ASTBinary elm) {
					switch (elm.getOp()){
						case minus:
						case plus:
						case mul:
						case div:
						case mod:
							if(isValidType(state,elm) && checkIt(elm, where)){
								flag[0] = true;
								setExprVarsTimeRelated(elm, where);
								elm.setTimeCritical(true);
							}
					}
				}
			};
			visit.setExcludePars(true);
			visit.setExcludeHiddenClass(true);
			expr.visit(visit);
			if(!flag[0]){
				//check x = timeVar;
				if(expr instanceof ASTLiteral){
					String varName = ((ASTLiteral) expr).getValue();
					flag[0] = where.existVarNameTimeRelevant(varName);
					if(flag[0])
						expr.setTimeCritical(true);
				}
			}
		}
		return flag[0];
	}

	public static boolean checkMinMaxTime(ASTMethodCall elm, Env where, ASTRE state) {
		boolean f = false;
		for(IASTRE exp : elm.getParameters()){
			if(CheckExpression.checkRightHandAssignment(state, exp, where)){
				elm.setMaxMin(true);
				elm.setTimeCritical(true);
				f = true;
			}
		}
		return f;
	}

	private static boolean isValidType(ASTRE state, IASTRE elm) {
		String type = state.getType();
		if(type != null && (type.equals("String") || type.equals("java.lang.String"))){
			return false;
		}
		if(type != null && (type.equals("boolean") || type.equals("Boolean"))){
			return false;
		}
		boolean[] flag = {true};
		DefualtASTREVisitor v = new DefualtASTREVisitor() {
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				if(elm.getMethodName().equals("toString")){
					flag[0] = false;
				}
			}
		};
		v.setExcludeHiddenClass(true);
		elm.visit(v);
		return flag[0];
	}

	private static void setExprVarsTimeRelated(IASTRE elm, Env where){
		elm.visit(new DefualtASTREVisitor() { //and also all the var used inside the expr

			List<String> varToSkip = new ArrayList<>();

			@Override
			public void enterASTAttributeAccess(ASTAttributeAccess elm) {
				varToSkip.add(elm.getVariableName().getCode());
			}

			@Override
			public void enterASTLiteral(ASTLiteral elm) {
				if(varToSkip.contains(elm.getValue())) return;

				IASTVar var = where.getVar(elm.getValue());
				if(var != null) { //avoid method call that can be literal as well
					var.setTimeCritical(true);
				}
			}
		});
	}
	/**
	 * The method accept a {@link IASTRE} expression and an {@link Env}.
	 * It checks if the expression is a time relevant expression.
	 * The checks that the method performs are regarding to:
	 * <ul>
	 *     <li>There is a call to a method that is time relevant</li>
	 *     <li>There is a usage of a name of variable that is time relevant</li>
	 * </ul>
	 * @param elm	The expression where to perform the check
	 * @param where	The envirorment to use for the search
	 * @return		true if the expression contains time relevant objects
	 */
	public static boolean checkIt(IASTRE elm, Env where) {
		final boolean[] r = {false};
		DefualtASTREVisitor v = new DefualtASTREVisitor(){
			@Override
			public void enterASTLiteral(ASTLiteral literal) {
				if(where.existVarNameTimeRelevant(literal.getValue()) ) {//and time critical
					r[0] = true;
					literal.setTimeCritical(true);
				}
			}

			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				if(elm.getClassPointed() != null && !elm.getClassPointed().equals("")){
					if(where.existMethodTimeRelevant(elm.getClassPointed(), elm.getMethodName(), getSignature(elm.getParameters(), where))){
						r[0] = true;
						elm.setTimeCritical(true);
					}
				}
				else if(where.existMethodTimeRelevant(elm.getMethodName(), getSignature(elm.getParameters(), where))){
					r[0] = true;
					elm.setTimeCritical(true);
				}
			}

		};
		v.setExcludeHiddenClass(true);
		v.setExcludePars(true);
		elm.visit(v);
		return r[0];
	}

	public static boolean checkBooleanTimeComparison(IASTRE expr, Env env){
		boolean[] find = {false};
		//search for A {<,<=,>,>=} C
		DefualtASTREVisitor v = new DefualtASTREVisitor(){
			@Override
			public void enterASTbinary(ASTBinary elm) {
				switch (elm.getOp()){
					case less:
					case lessEqual:
					case greater:
					case greaterEqual:
					case equality:
					case notEqual:
						if(CheckExpression.checkIt(elm, env)){
							//expr.setTimeCritical(true);
							elm.setTimeCritical(true);
							find[0] = true;
						}
				}
			}
		};
		v.setExcludeHiddenClass(true);
		expr.visit(v);
		return find[0];
	}

	private static List<String> getSignature(List<IASTRE> parameters, Env where){
		List<String> out = new ArrayList<>();
		for(IASTRE p : parameters){
			out.add(where.getExprType(p));
		}
		return out;
	}
}
