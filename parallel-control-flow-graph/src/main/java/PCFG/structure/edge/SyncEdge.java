package PCFG.structure.edge;

import PCFG.structure.node.INode;
import PCFG.structure.node.Node;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class SyncEdge implements IEdge {

	public class MalformedSyncEdge extends Exception {
		public MalformedSyncEdge(String message) {
			super(message + " :: [" + from + " -> " + to + "]");
		}
	}

	public enum TYPE {
		SYNC_BLOCK,
		MUST_SYNC,
		MAY_SYNC
	}

	INode from;
	INode to;
	TYPE type;
	String label;

	public SyncEdge(INode from, INode to) throws MalformedSyncEdge {
		this.from = from;
		this.to = to;
		if(from == null || to == null){
			//TODO fix this null that can appears
			System.out.println("Breakpoint");
		}
		if(!from.getClass().equals(to.getClass())){
			throw new MalformedSyncEdge("From and To nodes are not of the same kind");
		}
		type = from.getClass() == Node.class ? TYPE.MAY_SYNC : TYPE.SYNC_BLOCK;
	}

	public SyncEdge(INode from, INode to, TYPE t) throws MalformedSyncEdge {
		this.from = from;
		this.to = to;
		if(from == null || to == null){
			//TODO fix this null that can appears
			System.out.println("Breakpoint");
		}
		if(!from.getClass().equals(to.getClass())){
			throw new MalformedSyncEdge("From and To nodes are not of the same kind");
		}
		type = t;
	}

	public INode getFrom() {
		return from;
	}

	public INode getTo() {
		return to;
	}

	public TYPE getType() {
		return type;
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
