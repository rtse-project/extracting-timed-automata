package PCFG.structure.node;

import PCFG.structure.edge.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class SyncNode implements INode {

	public static int _ID = 0;
	int id = 0;
	private String expr;
	int start,end,line;
	String className;
	List<Node> nodes = new ArrayList<>();
	List<Node> nodesEntered = new ArrayList<>();
	List<Edge> newEdges = new ArrayList<>();
	public static final String _CLUSTER_NAME = "cluster_sync";


	public SyncNode(String expr, int start, int end, int line, String className) {
		this.id = _ID++;
		this.expr = expr;
		this.start = start;
		this.end = end;
		this.line = line;
		this.className = className;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public void add(Node node){
		this.nodes.add(node);
	}

	@Override
	public String getName() {
		return "sync_block";
	}

	@Override
	public int getID() {
		return id;
	}

	public String getExpr() {
		return expr;
	}

	public int getLine() {
		return line;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getClassName() {
		return className;
	}

	public void setNewEdges(List<Edge> e){
		this.newEdges = e;
	}

	public List<Edge> getNewEdges() {
		return newEdges;
	}

	public List<Node> getNodesEntered() {
		return nodesEntered;
	}

	public void setNodesEntered(List<Node> nodesEntered) {
		this.nodesEntered = nodesEntered;
	}
}
