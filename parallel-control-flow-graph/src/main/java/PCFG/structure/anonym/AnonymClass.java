package PCFG.structure.anonym;

import PCFG.structure.CFG;
import PCFG.structure.IHasCFG;
import PCFG.structure.node.INode;
import PCFG.structure.node.Node;
import PCFG.structure.node.SyncNode;

import java.util.ArrayList;
import java.util.List;

/**
 * With this class we represent the PCFG for an anonymous class.
 * We have a CFG for each method.
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class AnonymClass implements INode, IHasCFG {

	private static int ID = 0;
	private int id;
	private List<CFG> methods = new ArrayList<>();
	public static final String _CLUSTER_NAME = "cluster_anonymcfg_";

	public AnonymClass() {
		this.id = ID++;
	}

	@Override
	public String getName() {
		return "anonymousClass_" + this.id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public void addCFG(CFG cfg) {
		this.methods.add(cfg);
	}

	@Override
	public List<CFG> getCFG() {
		return this.methods;
	}

	public List<Node> AllNodes() {
		List<Node> nodes = new ArrayList<>();
		for(CFG c : methods){
			nodes.addAll(c.getAllNodes());
		}
		return nodes;
	}


	public List<SyncNode> getSyncNodes() {
		List<SyncNode> nodes = new ArrayList<>();
		for(CFG c : methods){
			nodes.addAll(c.getSyncNodes());
		}
		return nodes;
	}

	public void setStartEnd() {
		for(CFG m : methods){
			m.setStartEnd();
		}
	}
}
