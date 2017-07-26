package PCFG.converter;

import PCFG.structure.CFG;
import PCFG.structure.PCFG;
import PCFG.structure.anonym.AnonymClass;
import PCFG.structure.edge.AnonymEdge;
import PCFG.structure.edge.Edge;
import PCFG.structure.edge.IEdge;
import PCFG.structure.edge.SyncEdge;
import PCFG.structure.node.INode;
import PCFG.structure.node.Node;
import PCFG.structure.node.SyncNode;

/**
 * The class convert the PCFG in the DOT language
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ToDot implements IConverter {

	private boolean hideName = false;
	private int tabs = 1;

	/**
	 * Constructor. Initialise the internal variables according to the parameters.
	 * @param hideName If the flag is set to true, the names are converted with a progressive number
	 */
	public ToDot(boolean hideName) {
		this.hideName = hideName;
	}

	/**
	 * Return if the names are replaced with a progressive number.
	 * @return Boolean value.
	 */
	public boolean isHideName() {
		return hideName;
	}

	/**
	 * Set if the rendering has to use normal names to represent the states or it has to use a progressive number.
	 * @param hideName If true the rendering will produce names with a progressive number
	 */
	public void setHideName(boolean hideName) {
		this.hideName = hideName;
	}

	/**
	 * PrettyPrint the PCFG in the Graphviz syntax
	 * @param pcfg PCFG to convert.
	 * @return	Graphviz representation of the PCFG
	 */
	@Override
	public String convert(PCFG pcfg) {
		StringBuilder out = new StringBuilder();
		out.append("digraph {\n\trankdir=TD;\n\tcompound=true;\n");
		for(CFG process : pcfg.getCFG()){
			out.append(convert(process));
		}
		for(SyncEdge sEdge : pcfg.getESync()){
			out.append(tabs() + convert(sEdge));
		}
		out.append("}\n");
		return out.toString();
	}

	private String convert(CFG cfg){
		StringBuilder out = new StringBuilder();
		out.append(tabs() + "subgraph " + CFG._CLUSTER_NAME + cfg.getID() + " {\n");
		tabs++;
		out.append(tabs() + "node [style=filled];\n");
		for(Node v :  cfg.getV()){
			String color = "";
			if(v.getConstraint() != null){
				color = " color=salmon2, ";
			}
			if(v.isStart()){
				color = " style=\"dashed\" ";
			}
			if(v.isEnd()){
				color = " color=\"black\", fontcolor=white, ";
			}
			String label = "label=\"" + v.getNameNoID() + "\"";
			out.append(tabs() + convert(v) + "[" + color +  label + "];\n");
		}
		for(SyncNode s : cfg.getSyncNodesNoSubClass()){
			out.append(convert(s));
		}
		for(IEdge e : cfg.getE()){
			out.append(tabs() +  convert(e));
		}
		for(AnonymClass a : cfg.getAnonNodes()){
			out.append(convert(a));
		}
		for(AnonymEdge ae : cfg.getAnonEdge()){
			out.append(tabs() +  convert(ae));
		}
		out.append(tabs() + "label = \"" + cfg.getName() + "_" + cfg.getID() + "\";\n");
		out.append(tabs() + "color=black\n");
		tabs--;
		out.append(tabs() + "}\n");
		return out.toString();
	}

	private String convert(AnonymClass a) {
		StringBuilder out = new StringBuilder();
		out.append(tabs() + "subgraph " + AnonymClass._CLUSTER_NAME +  a.getID() + " {\n");
		tabs++;
		out.append(tabs() + "node [style=filled];\n");
		for(CFG m : a.getCFG()){
			out.append(convert(m));
		}
		out.append(tabs() + "label = \"" + a.getName() + "\";\n");
		out.append(tabs() + "color=black, style=bold\n");
		tabs--;
		out.append(tabs() + "}\n");
		return out.toString();
	}

	private String convert(IEdge e) {
		if(e instanceof Edge){
			return convert((Edge)e);
		} else if(e instanceof AnonymEdge){
			return convert((AnonymEdge)e);
		} else {
			return convert((SyncEdge)e);
		}
	}

	private String convert(Edge e) {
		StringBuilder out = new StringBuilder();
		out.append(convert(e.getFrom()) + " -> " + convert(e.getTo()));
		out.append("[ label = \"" + e.getLabel() + "\" ]");
		out.append(";\n");
		return out.toString();
	}

	private String convert(AnonymEdge e) {
		StringBuilder out = new StringBuilder();
		if(e.getTo() instanceof AnonymClass && ((AnonymClass) e.getTo()).getCFG().size() > 0){
			AnonymClass ac = (AnonymClass)e.getTo();
			CFG tmp = ac.getCFG().get(0);
			if(tmp.getV().size() > 0) {
				out.append(convert(e.getFrom()) + " -> " + convert( tmp.getV().get(0)));
				out.append("[ lhead = \"" + AnonymClass._CLUSTER_NAME + e.getTo().getID() + "\" ]");
				out.append(";\n");
			}
		}
		return out.toString();
	}

	private String convert(SyncEdge e) {
		StringBuilder out = new StringBuilder();

		if(e.getType() == SyncEdge.TYPE.SYNC_BLOCK){
			SyncNode from 	= ((SyncNode) e.getFrom());
			SyncNode to 	= ((SyncNode) e.getTo());
			Node f = from.getNodes().get(0);
			Node t = to.getNodes().get(0);
			out.append(convert(f) + " -> " + convert(t) + " [ltail=" + SyncNode._CLUSTER_NAME + from.getID() + ",lhead=" + SyncNode._CLUSTER_NAME + to.getID() + ", color=black,penwidth=1.0,dir=\"both\", style=\"dashed\",constraint=false];\n");
		} else {
			Node from = (Node) e.getFrom();
			Node to   = (Node) e.getTo();
			out.append(convert(from) + " -> " + convert(to) + "[dir=both,color=black, style=bold,penwidth=1.0,constraint=false];\n");
		}

		return out.toString();
	}

	private String convert(INode s) {
		if(s instanceof Node){
			return convert((Node)s);
		} else {
			return convert((SyncNode)s);
		}
	}

	private String convert(SyncNode s) {
		StringBuilder out = new StringBuilder();
		out.append(tabs() + "subgraph " + SyncNode._CLUSTER_NAME + s.getID() + " {\n");
		tabs++;
		out.append(tabs() + "style=dashed;\n");
		out.append(tabs() + "node [style=filled];\n");
		for(Node v : s.getNodes()){
			out.append(tabs() + convert(v) + ";\n");
		}
		out.append(tabs() + "label = \"sync block on " + s.getExpr() + "\";\n");
		out.append(tabs() + "color=black\n");
		tabs--;
		out.append(tabs() + "}\n");
		return out.toString();
	}

	private String convert(Node v){
		String name;
		if(hideName) {
			name = "s" + v.getID();
		} else {
			name = v.getName();
		}
		return name;
	}

	private String tabs(){
		String out = "";
		for(int i = 0 ; i < this.tabs; i++){
		 	out += "\t";
		}
		return out;
	}
}
