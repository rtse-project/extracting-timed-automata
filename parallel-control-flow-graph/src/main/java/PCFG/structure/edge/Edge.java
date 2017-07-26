package PCFG.structure.edge;

import PCFG.structure.node.Node;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class Edge implements IEdge {
	Node from;
	Node to;
	String label;
	private Constraint constraint;

	boolean isResetClock = false;
	List<Pair<String,String>> resetVars = new ArrayList<>();

	public boolean isResetClock() {
		return isResetClock;
	}

	public void setResetClock(boolean resetClock) {
		this.setResetClock("t", resetClock);
	}

	public void setResetClock(String s, boolean resetClock) {
		this.setResetClock(s, resetClock, "");
	}
	public void setResetClock(String s, boolean resetClock, String value) {
		this.isResetClock = resetClock;
		this.resetVars.add(new Pair<String,String>(s,value));
	}

	public void addResetVar(Pair<String,String> var){
		resetVars.add(var);
	}

	public void addResetVar(String name, String value){
		resetVars.add(new Pair<>(name,value));
	}

	public List<Pair<String,String>> getResetVars() {
		return resetVars;
	}

	public Edge(Node from, Node to) {
		this.from = from;
		this.to = to;
		this.label = "";
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setResetVars(List<Pair<String,String>> resetVar) {
		this.resetVars = resetVar;
	}
}
