package intermediateModelHelper.heuristic.definition;

import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTConstructor;
import intermediateModel.structure.ASTMethod;
import intermediateModel.structure.ASTRE;
import intermediateModel.structure.expression.ASTMethodCall;
import intermediateModel.visitors.DefualtASTREVisitor;
import intermediateModelHelper.envirorment.Env;
import intermediateModelHelper.envirorment.temporal.TemporalInfo;
import intermediateModelHelper.envirorment.temporal.structure.ImplicitResourceTimeout;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 */
public class ImplicitTimeComparisonApplication extends SearchTimeConstraint {

	//TODO: Refactor names
	class struct {
		int start;
		int end;
		int line;

		public struct(int start, int end, int line) {
			this.start = start;
			this.end = end;
			this.line = line;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			struct struct = (struct) o;

			if (start != struct.start) return false;
			if (end != struct.end) return false;
			return line == struct.line;
		}
	}

	List<ImplicitResourceTimeout>  timeMethods = TemporalInfo.getInstance().getImplicitResourceTimeouts();

	IASTMethod currentMethod;
	String className;

	List<struct> find = new ArrayList<>();

	@Override
	public void setup(ASTClass c) {
		super.setup(c);
		className = c.getPackageName() + "." + c.getName();
	}

	@Override
	public void next(ASTRE stm, Env env) {
		//works only on ASTRE
		IASTRE expr = stm.getExpression();
		if(expr == null){
			return;
		}

		expr.visit(new DefualtASTREVisitor(){
			@Override
			public void enterASTMethodCall(ASTMethodCall elm) {
				String pointer = elm.getClassPointed();
				String name = elm.getMethodName();
				List<IASTRE> pars = elm.getParameters();
				int size = pars.size();
				if(pointer != null && containTimeMethod(pointer, name, size)) {
					markMethod(elm);
				}
			}
		});

	}

	private void markMethod(ASTMethodCall elm) {
		struct e = new struct(elm.getStart(), elm.getEnd(), elm.getLine());
		if(find.contains(e)) return;
		find.add(e);
		super.addConstraint(elm.print(), elm, false);
	}

	private boolean containTimeMethod(String pointer, String name, int nPars){
		for(ImplicitResourceTimeout m : timeMethods){
			if(m.getClassName().equals(pointer) && m.getMethodName().equals(name) && m.getSignature().size() == nPars)
				return true;
		}
		return false;
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

}
