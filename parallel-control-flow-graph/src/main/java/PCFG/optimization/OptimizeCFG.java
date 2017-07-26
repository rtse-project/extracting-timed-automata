package PCFG.optimization;

import PCFG.structure.CFG;
import PCFG.structure.IOptimization;
import PCFG.structure.PCFG;
import PCFG.structure.anonym.AnonymClass;
import PCFG.structure.edge.Edge;
import PCFG.structure.node.Node;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class OptimizeCFG implements IOptimization {
	@Override
	public PCFG optimize(PCFG pcfg) {
		for(CFG p : pcfg.getCFG()){
			handleCFG(p);
		}
		return pcfg;
	}

	private void handleCFG(CFG p) {
		if(p.getV().size() == 1){
			Node first = p.getV().get(0);
			//only one node. Create an empty init
			Node init = new Node("init","nop", Node.TYPE.USELESS, 0,0,-1);
			p.getV().add(init);
			Edge e = new Edge(init, first);
			p.getE().add(e);
			init.setStart(true);
			first.setStart(false);
			first.setEnd(true);
			if(first.getConstraint() != null){
				e.setConstraint(first.getConstraint());
			}
		}

		List<Edge> edges = new ArrayList<>();
		for(Edge e : p.getE()){
			//remove the edge out of a return or throw
			if(	!e.getFrom().getType().equals(Node.TYPE.RETURN) &&
				!e.getFrom().getType().equals(Node.TYPE.THROW)	)
			{
				edges.add(e);
			}
		}
		p.setE(edges);
		for(Node n : p.getAllNodes()){
			boolean f = false;
			for(Edge e : p.getE()){
				if(e.getFrom().equals(n)){
					f = true;
				}
			}
			n.setEnd(!f); //set end node only when there are no exit edges
			//move cnst to edge
			if(n.getConstraint() != null){
				for(Edge e : p.getE()){
					if(e.getFrom().equals(n)){
						if( //is a branching stms?
							(n.getType() == Node.TYPE.IF_EXPR || n.getType() == Node.TYPE.WHILE_EXPR)
									&&
							e.getLabel().equals("False"))
						{
							Constraint c = n.getConstraint();
							e.setConstraint(c.negate());
						} else {
							e.setConstraint(n.getConstraint());
						}
					}
				}
			}
		}

		handleBrk(p);
		handleUseless(p);
		handleFinal(p);

		List<Edge> newE = new ArrayList<>();
		//remove useless edges
		for(Edge e : p.getE()){
			Node from = e.getFrom();
			if(p.getV().contains(from)){
				newE.add(e);
			}
		}
		p.setE(newE);

		for(AnonymClass a : p.getAnonNodes()){
			for(CFG c : a.getCFG()){
				handleCFG(c);
			}
		}
	}

	private void handleFinal(CFG p) {
		for(Node v : p.getV()) {
			boolean flag = true;
			for (Edge e : p.getE()) {
				if (e.getFrom().equals(v))
					flag = false;
			}
			v.setEnd(flag);
		}
	}

	private void handleBrk(CFG p) {
		List<Edge> newEdges = new ArrayList<>(p.getE());
		for(Edge e : p.getE()){
			if(e.getFrom().getType() == Node.TYPE.BREAK && e.getTo().getType() != Node.TYPE.END_CICLE){
				newEdges.remove(e);
			}
		}
		p.setE(newEdges);
	}
	private void handleUseless(CFG p) {
		List<Node> newNode = new ArrayList<>();
		for(Node v : p.getV()){
			boolean flag = false;
			if(v.getType().equals(Node.TYPE.END_IF)) {
				for(Edge e : p.getE()){
					if(e.getTo().equals(v))
						flag = true;
				}
				if(flag){
					newNode.add(v);
				}
			} else {
				newNode.add(v);
			}

		}
		p.setV(newNode);
	}
}
