package PCFG.optimization;

import PCFG.structure.CFG;
import PCFG.structure.IOptimization;
import PCFG.structure.PCFG;
import PCFG.structure.anonym.AnonymClass;
import PCFG.structure.edge.Edge;
import PCFG.structure.node.Node;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.heuristic.definition.TimeInSignature;
import intermediateModelHelper.heuristic.definition.AssignmentTimeVar;
import intermediateModelHelper.heuristic.definition.TimeoutResources;
import intermediateModelHelper.heuristic.definition.UndefiniteTimeout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class OptimizeTimeAutomata implements IOptimization {


	@Override
	public PCFG optimize(PCFG pcfg) {
		for(CFG p : pcfg.getCFG()){
			handleCFG(p);
		}
		return pcfg;
	}

	private void handleCFG(CFG c) {

		for(Node v : c.getV()){
			if(v.getConstraint() != null) {
				List<Node> n = c.getPrev(v);
				if(n.size() == 1 && n.get(0).getLine() == -1){
					//get edge
					Edge e = c.getE().get(0);
					e.getFrom().setResetVars(e.getTo().getResetVars());
					e.getFrom().setResetClockOnly(true);
				}
			}
		}

		//fix for timeout as last node
		boolean toAdd = false;
		Node end = null;
		Edge toEnd = null;
		for(Node v : c.getV()){
			if(v.getConstraint() != null) {
				List<Node> n = c.getNext(v);
				if(n.size() == 0 || n.get(0).equals(v)){
					//add new node
					if(!v.getConstraint().isCategory(AssignmentTimeVar.class)) {
						//v.setResetClockOnly(true);
						v.setResetClock(false);
					}

					end = new Node("endMethod", "", Node.TYPE.USELESS, 0, 0, 0);
					toEnd = new Edge(v, end);
					toEnd.setConstraint(v.getConstraint());
					toAdd = true;
					if(v.getConstraint().isCategory(AssignmentTimeVar.class)) {
						//v.setResetClockOnly(true);
						v.getLine();
					}

				}
			}
		}
		if(toAdd){
			c.getV().add(end);
			c.getE().add(toEnd);
		}

		// annotate reset of variables
		handleNodes(c.getV());
		handleEdges(c.getE());
		for(AnonymClass a : c.getAnonNodes()){
			for(CFG ac : a.getCFG()){
				handleCFG(ac);
			}
		}


	}

	private void handleNodes(List<Node> V){
		for(Node n : V){
			if(n.getConstraint() != null) {
				if(n.getConstraint().isCategory(TimeInSignature.class)){
					//n.setResetClock(true);
					n.getConstraint().setValue("t >= " + filterValue(n.getConstraint().getValue()));
				}
			}
		}
	}

	private void handleEdges(List<Edge> E){
		List<Edge> newE = new ArrayList<>();
		for(Edge e : E){
			Node n = e.getTo();
			if(n.getConstraint() != null) {
				if(n.getConstraint().isCategory(TimeInSignature.class)){
					e.getFrom().setResetClock(true);
					e.setResetClock(true);
					//n.getConstraint().setValue("t <= " + n.getConstraint().getValue());
				}
				else if(n.getConstraint().isCategory(TimeoutResources.class)){

				}
				else if(n.getConstraint().isCategory(UndefiniteTimeout.class)){
					newE.add(new Edge(n,n));
				}
			}
			if(e.getFrom().getLine() == -1){
				//init transition
				e.setConstraint(null);
			}
			Constraint c = e.getFrom().getConstraint();
			//we only looking forward, what about the first??
			if( c != null && c.isCategory(UndefiniteTimeout.class) && e.getFrom().isStart()){
				newE.add(new Edge(e.getFrom(),e.getFrom()));
			}
		}
		E.addAll(newE);
	}


	private String filterValue(String v){
		StringBuilder out = new StringBuilder();

		Pattern pp = Pattern.compile("(?<sign>\\*|\\+|-|/)");
		Matcher mm = pp.matcher(v);
		List<String> sign = new ArrayList<>();
		while(mm.find()){
			sign.add(mm.group("sign"));
		}
		String[] matches = v.split("(?<sign>\\*|\\+|-|/)");
		int i = 0;
		for(String mat : matches) {
			if (mat.trim().substring(0, 1).matches("[0-9]")) {
				out.append(mat.replaceAll("[aA-zZ]+", ""));
			} else {
				out.append(mat);
			}
			if(i < sign.size()){
				out.append(sign.get(i));
				i++;
			}
		}

		/*
		String[] matches = v.split();
		for(String m : matches) {
			if (m.substring(0, 1).matches("[0-9]")) {
				out.append(m.replaceAll("[aA-zZ]+", ""));
			}
		}
		*/
		return out.toString();
	}
}
