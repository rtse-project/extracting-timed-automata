package PCFG.optimization;

import PCFG.structure.CFG;
import PCFG.structure.IOptimization;
import PCFG.structure.PCFG;
import PCFG.structure.edge.Edge;
import PCFG.structure.node.Node;
import PCFG.structure.node.SyncNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class OptimizeForXAL implements IOptimization {
	@Override
	public PCFG optimize(PCFG pcfg) {
		for(CFG p : pcfg.getCFG()){
			for(SyncNode n : p.getSyncNodes()){
				removeNodeAlreadyInSync(n,p);
			}

		}
		return pcfg;
	}

	private void removeNodeAlreadyInSync(SyncNode n, CFG p){
		//collect all nodes in sync
		List<Node> nodesInSync = new ArrayList<>();
		//for(SyncNode n : p.getSyncNodes()){
		nodesInSync.addAll( n.getNodes() );
		//}
		//remove from the main automata
		List<Node> newV = new ArrayList<>();
		newV.addAll( p.getV() );
		for(Node v : p.getV() ){
			if(nodesInSync.contains(v)){
				newV.remove(v);
			}
		}
		//set init and last
		if(newV.size() > 0) newV.get(0).setStart(true);
		if(newV.size() > 0) newV.get(newV.size()-1).setEnd(true);

		if(nodesInSync.size() > 0) nodesInSync.get(0).setStart(true);
		if(nodesInSync.size() > 0) nodesInSync.get(nodesInSync.size()-1).setEnd(true);

		p.setV(newV);
		//bypass the arrows
		List<Node> fromArrow = new ArrayList<>();
		List<Node> toArrow = new ArrayList<>();
		List<Edge> toRemove = new ArrayList<>();
		List<Edge> toAdd = new ArrayList<>();

		for(Edge e : p.getE()){
			if(!nodesInSync.contains(e.getFrom()) && nodesInSync.contains(e.getTo())){
				fromArrow.add(e.getFrom());
			}
			if(nodesInSync.contains(e.getFrom()) && !nodesInSync.contains(e.getTo())){
				toArrow.add(e.getTo());
			}
			if(nodesInSync.contains(e.getFrom()) || nodesInSync.contains(e.getTo())){
				toRemove.add(e);
			}
			if(nodesInSync.contains(e.getFrom()) && nodesInSync.contains(e.getTo())){
				toAdd.add(e);
			}
		}
		for(Edge e : toRemove){
			p.getE().remove(e);
		}
		n.setNewEdges(toAdd);
		n.setNodesEntered(fromArrow);
		for(Node f : fromArrow){
			for(Node t : toArrow){
				p.addEdge(
						new Edge(f,t)
				);
			}
		}
	}
}
