package PCFG.structure;

import PCFG.converter.ToDot;
import PCFG.optimization.OptimizeCFG;
import PCFG.structure.edge.Edge;
import PCFG.structure.edge.SyncEdge;
import PCFG.structure.node.INode;
import PCFG.structure.node.Node;
import PCFG.structure.node.SyncNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class handle the Parallel Control Flow Graph representation.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class PCFG implements IHasCFG {

	public enum DefaultAnnotation {
		GlobalVars
	}

	HashMap<String,Object> annotation = new HashMap<>();

	List<SyncEdge> ESync = new ArrayList<>();
	List<CFG> processes = new ArrayList<>();

	public PCFG() {
	}

	/**
	 * Insert a synchronization edge.
	 * @param edge Edge to add.
	 */
	public void addSyncEdge(SyncEdge edge){
		INode f = edge.getFrom();
		INode t = edge.getTo();
		boolean flag = true;
		for(SyncEdge e : ESync){
			if(e.getFrom().equals(f) && e.getTo().equals(t))
				flag = false;
			if(e.getFrom().equals(t) && e.getTo().equals(f))
				flag = false;
		}
		if(flag)
			this.ESync.add(edge);
	}

	/**
	 * Insert a {@link CFG}.
	 * Each process/thread has its own Control Flow Graph.
	 * @param cfg Control flow graph to add.
	 *
	 * @see CFG
	 */
	public void addCFG(CFG cfg){
		processes.add(cfg);
	}

	/**
	 * Get the list of {@link SyncNode} among all the CFGs.
	 * @return	list of {@link SyncNode} among all the CFGs.
	 */
	public List<SyncNode> getSyncNodes(){
		List<SyncNode> out = new ArrayList<>();
		for(CFG cfg : processes){
			out.addAll( cfg.getSyncNodes() );
		}
		return out;
	}

	/**
	 * Get list of {@link Node} among all the CFGs.
	 * @return list of {@link Node} among all the CFGs.
	 */
	public List<Node> getV() {
		List<Node> out = new ArrayList<>();
		for(CFG cfg : processes){
			out.addAll( cfg.getAllNodes() );
		}
		return out;
	}

	/**
	 * Get list of {@link Edge} among all the CFGs.
	 * @return list of {@link Edge} among all the CFGs.
	 */
	public List<Edge> getE() {
		List<Edge> out = new ArrayList<>();
		for(CFG cfg : processes){
			out.addAll( cfg.getE() );
		}
		return out;
	}

	/**
	 * Get list of {@link SyncEdge} in the PCFG.
	 * @return list of {@link SyncEdge} in the PCFG.
	 */
	public List<SyncEdge> getESync() {
		return ESync;
	}

	/**
	 * Get the list of CFGs in the PCFG.
	 * @return List of {@link CFG}
	 */
	public List<CFG> getCFG() {
		return processes;
	}

	/**
	 * TODO: Implementing the optimization
	 */
	public void optimize(){
		for(CFG p : processes){
			p.setStartEnd();
		}
		this.optimize(new OptimizeCFG());
	}

	public PCFG optimize(IOptimization optimizer){
		return optimizer.optimize(this);
	}

	public String toGraphViz(boolean hideNames){
		ToDot toGraphViz = new ToDot(hideNames);
		return toGraphViz.convert(this);
	}

	/**
	 * From an expression, retrieve the correlative synchronization node.
	 * @param expr			Src code of the expression
	 * @param start			Src code of the expression
	 * @param end			Src code of the expression
	 * @param line			Line number of the node
	 * @param hash 			Hashcode of the class where to search for the Sync node
	 * @return	The syncnode.
	 */
	public SyncNode getSyncNodeByExpr(String expr, int start, int end, int line, int hash){
		for(CFG c : this.getCFG()){
			if(c.getHashcode() == hash){
				for(SyncNode s : c.getSyncNodes()){
					if(
							s.getExpr().equals(expr) &&
									//s.getClassName().equals(className) &&
									s.getLine() == line &&
									s.getStart() == start &&
									s.getEnd() == end
							) {
						return s;
					}
				}
			}
		}

		return null;
	}

	/**
	 * From an expression, retrieve the correlative synchronization node.
	 * @param expr			Src code of the expression
	 * @param start			Src code of the expression
	 * @param end			Src code of the expression
	 * @param line			Line number of the node
	 * @param skip 			Number of node to skip
	 * @return	The syncnode.
	 */
	public SyncNode getSyncNodeByExprSkip(String expr, int start, int end, int line, int skip){
		for(SyncNode s : this.getSyncNodes()){
			if(
					s.getExpr().equals(expr) &&
							//s.getClassName().equals(className) &&
							s.getLine() == line &&
							s.getStart() == start &&
							s.getEnd() == end
					) {
				if(skip <= 0){
					return s;
				} else {
					skip--;
				}
			}
		}
		return null;
	}

	public HashMap<String, Object> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(HashMap<String, Object> annotation) {
		this.annotation = annotation;
	}

	public void addAnnotation(String key, Object attrs) {
		this.annotation.put(key, attrs);
	}
}
