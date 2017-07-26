package PCFG.structure.edge;

import PCFG.structure.node.INode;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class AnonymEdge implements IEdge {
	INode from;
	INode to;
	String label;

	public AnonymEdge(INode from, INode to) {
		this.from = from;
		this.to = to;
		this.label = "";
	}

	public INode getFrom() {
		return from;
	}

	public INode getTo() {
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

}
