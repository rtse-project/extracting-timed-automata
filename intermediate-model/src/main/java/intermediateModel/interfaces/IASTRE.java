package intermediateModel.interfaces;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface IASTRE extends IASTVisitor, IASTToken {
	enum OPERATOR {
		less,			// <
		lessEqual,		// <=
		notEqual,		// !=
		equal,			// =
		equality,		// ==
		greater,		// >
		greaterEqual,	// >=
		shiftLeft,		// <<
		shiftRight,		// >>
		plus,			// +
		minus,			// -
		mul,			// *
		div,			// /
		not,			// !
		mod,			// %
		and,			// &&
		or,				// ||
		xor,			// ^
		instanceOf,		// instanceOf
		empty;


		public String print(){
			switch (this){
				case less: return "<";
				case lessEqual: return "<=";
				case notEqual: return "!=";
				case equal: return "=";
				case equality: return "==";
				case greater: return ">";
				case greaterEqual: return ">=";
				case shiftLeft: return "<<";
				case shiftRight: return ">>";
				case plus: return "+";
				case minus: return "-";
				case mul: return "*";
				case div: return "/";
				case not: return "!";
				case mod: return "%";
				case and: return "&&";
				case or: return "||";
				case instanceOf: return "instanceOf";
			}
			return "";
		}

		@Override
		public String toString() {
			if(this.equals(OPERATOR.equality))
				return "equal";
			return super.toString();
		}
	}

	enum ADDDEC {
		increment,
		decrement;

		public String print(){
			switch (this){
				case decrement: return "--";
				case increment: return "++";
			}
			return "";
		}
	}

	void visit(ASTREVisitor visitor);
	String getCode();
	int getStart();
	int getEnd();
	int getLine();
	int getLineEnd();
	boolean isTimeCritical();
	void setTimeCritical(boolean timeCritical);
	IASTRE negate();

	String print();
	boolean equals(Object o);
}
