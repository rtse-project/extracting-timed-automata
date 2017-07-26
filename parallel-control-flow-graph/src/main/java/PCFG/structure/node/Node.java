package PCFG.structure.node;

import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.ASTRE;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class Node implements INode {
	String name;
	String code;
	TYPE type;
	int id = 0;
	int start,end,line;
	Constraint constraint = null;
	boolean isResetClock = false;
	boolean isStart = false;
	boolean isEnd = false;

	List<Pair<String,String>> resetVars = new ArrayList<>();

	public static int _ID = 0;

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean start) {
		isStart = start;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean end) {
		isEnd = end;
	}

    public void setResetVars(List<Pair<String,String>> resetVars) {
        this.resetVars = resetVars;
    }


    public enum TYPE {
		RETURN,
		BREAK,
		CONTINUE,
		FOREACH,
		THROW,
		TRY,
		FINALLY,
		SWITCH,
		NORMAL,
		USELESS,
		HIDDENCLASS,
		IF_EXPR,
		CONTAINS_SYNC,
		END_WHILE,
		WHILE_EXPR,
		END_CICLE, END_IF,
	}

	public Node(String name, String code, TYPE type, int start, int end, int line) {
		this.name = name;
		this.code = code;
		this.type = type;
		this.id = _ID++;
		this.start = start;
		this.end = end;
		this.line = line;
	}

	public String getName() {
		return name + "_" + this.id;
	}

	public String getNameNoID() {
		String suf = "";
		if(this.type == TYPE.IF_EXPR){
			suf = "if ";
		}
		return  suf + name;// + "_" + this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public int getID() {
		return id;
	}

	public boolean equalsByCode(Node obj) {
		return obj.getCode().equals(this.code);
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getLine() {
		switch(this.type){
			case TRY:
			case BREAK:
			case THROW:
			case NORMAL:
			case RETURN:
			case SWITCH:
			case FOREACH:
			case FINALLY:
			case IF_EXPR:
			case CONTINUE:
			case WHILE_EXPR:
				return line;
		}
		return line;
	}

	public boolean isResetClock() {
		return isResetClock;
	}

	public void setResetClock(boolean resetClock) {
		this.setResetClock("t", resetClock);
	}

	public void setResetClockOnly(boolean resetClock) {
		this.isResetClock = resetClock;
	}

	public void setResetClock(String s, boolean resetClock) {
		this.setResetClock(s, resetClock, "");
	}
	public void setResetClock(String s, boolean resetClock, String value) {
		this.isResetClock = resetClock;
		this.resetVars.add(new Pair<String,String>(s,value));
	}

	public List<Pair<String,String>> getResetVars() {
		return resetVars;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Node)) return false;

		Node node = (Node) o;
		if (getID() != node.getID()) return false;
		if (getStart() != node.getStart()) return false;
		if (getEnd() != node.getEnd()) return false;
		if (getLine() != node.getLine()) return false;
		if (getCode() != null ? !getCode().equals(node.getCode()) : node.getCode() != null) return false;
		return getType() == node.getType();
	}

	public boolean equals(ASTRE r){
		if (getStart() != r.getStart()) return false;
		if (getEnd()   != r.getEnd()) return false;
		if (getLine()  != r.getLine()) return false;
		if (getCode()  != null ? !getCode().contains(r.getCode()) : r.getCode() != null) return false;
		return true;
	}

	public boolean equals(Constraint c){
		IASTStm r = c.getElm();
		if (getStart() != r.getStart()) return false;
		if (getEnd()   != r.getEnd()) return equalsExpectSemiColon(c);
		if (getLine()  != r.getLine()) return false;
		if (getCode()  != null ? !getCode().equals(r.getCode()) : r.getCode() != null) return false;
		return true;
	}

	public boolean weakEquals(Constraint c) {
		IASTStm r = c.getElm();
		if (getStart() >= r.getStart()) return false;
		if (getEnd()   <= r.getEnd()) return false;
		if (getLine()  != r.getLine()) return false;
		if (getCode()  != null ? !getCode().contains(r.getCode()) : r.getCode() != null) return false;
		return true;
	}

	private boolean equalsExpectSemiColon(Constraint c) {
		IASTStm r = c.getElm();
		if (getStart() != r.getStart()) return false;
		if (getEnd()-1   != r.getEnd()) return false;
		if (getLine()  != r.getLine()) return false;
		if (getCode()  != null ? !getCode().substring(0, getCode().length()-1).equals(r.getCode()) : r.getCode() != null) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = getCode() != null ? getCode().hashCode() : 0;
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + getStart();
		result = 31 * result + getEnd();
		result = 31 * result + line;
		return result;
	}
}
