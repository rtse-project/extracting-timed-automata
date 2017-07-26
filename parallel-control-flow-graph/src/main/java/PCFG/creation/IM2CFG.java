package PCFG.creation;

import PCFG.creation.helper.CreatePCFG;
import PCFG.structure.PCFG;
import PCFG.structure.node.Node;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTStm;
import intermediateModel.structure.ASTAttribute;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModelHelper.envirorment.temporal.structure.Constraint;
import intermediateModelHelper.envirorment.temporal.structure.RuntimeConstraint;
import intermediateModelHelper.heuristic.definition.TimeInSignature;
import intermediateModelHelper.heuristic.definition.AssignmentTimeVar;
import intermediateModelHelper.heuristic.definition.UndefiniteTimeout;
import org.javatuples.KeyValue;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * The following class convert the IM of a set of methods in a CFG first.
 * Then, it create cross links between the CFGs of the methods where there is:
 * <ul>
 *     <li>a synchronization on the same variables</li>
 *     <li>a method call on the same synchronized methods</li>
 * </ul>
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class IM2CFG {

	CreatePCFG pcfgBuilder = new CreatePCFG();
	private List<Constraint> constraints = new ArrayList<>();

	public IM2CFG(IASTMethod m, ASTClass c) {
		pcfgBuilder.addMethod(m, c);
	}

	public IM2CFG() {
	}

	public int getConstraintsSize() {
		return constraints.size();
	}

	public List<Constraint> getConstraints(){
		constraints.sort(Comparator.comparing(Constraint::getCategory));
		return constraints;
	}

	/**
	 * Insert a class in the list of classes to process for creating the PCFG.
	 * Moreover, the method will index the class and extract the time constraint in it.
	 * @param c				Class to analyze
	 * @param method		Method of the class to analyze
	 */
	public void addClass(ASTClass c, IASTMethod method){

		List<Constraint> currentClassConstraint = ApplyHeuristics.getConstraint(c);
		IASTMethod met = null;
		for(IASTMethod m : c.getMethods()){
			if(m.equals(method)){
				met = m;
			}
		}
		if(met != null){
			int startLine =  ((IASTStm) met).getLine();
			int endLine   =  ((IASTStm) met).getLineEnd();
			for(Constraint time : currentClassConstraint){
				int line = time.getLine();
				if(startLine <= line && line <= endLine){
					constraints.add(time);
				}
			}
		}
		KeyValue<IASTMethod, ASTClass> k = new KeyValue<>(method, c);
		pcfgBuilder.addMethod(k);
		//IndexingFile indexFile = new IndexingFile();
		//indexFile.index(c, reindex);
	}

	public PCFG buildPCFG(){

		PCFG pcfg = pcfgBuilder.convert();

		//annotate with attributes
		List<Pair<String,String>> attrs = new ArrayList<>();
		List<ASTClass> visited = new ArrayList<>();
		ASTClass tmp;
		for(KeyValue<IASTMethod,ASTClass> k : pcfgBuilder.getClasses()){
			tmp = k.getValue();
			if(!visited.contains(tmp)){
				visited.add(tmp);
				for(ASTAttribute a : tmp.getAttributes()){
					attrs.add(new Pair<>(a.getType(), a.getName()));
				}
			}
		}
		pcfg.addAnnotation(String.valueOf(PCFG.DefaultAnnotation.GlobalVars), attrs);


		for(Constraint c : constraints){
			for(Node v : pcfg.getV()){
				if(v.equals(c) || (v.getType() == Node.TYPE.WHILE_EXPR && v.weakEquals(c))){
					v.setConstraint(c);
				} else if(c.isCategory(UndefiniteTimeout.class) && v.weakEquals(c)){
					v.setConstraint(c);
				}
			}
		}
		//set time constraint


		//optimize
		pcfg.optimize();

		return pcfg;
	}


	public List<RuntimeConstraint> getRuntimeConstraints() {
		List<RuntimeConstraint> runtimeConstraints = new ArrayList<>();
		for(Constraint c : getConstraints()){
			for(RuntimeConstraint r : c.getRuntimeConstraints()){
				if(!runtimeConstraints.contains(r))
					runtimeConstraints.add(r);
			}
		}
		runtimeConstraints.sort(Comparator.comparing(RuntimeConstraint::getClassName).thenComparing(RuntimeConstraint::getLine));
		return runtimeConstraints;
	}
	public List<RuntimeConstraint> getResetRuntimeConstraints() {
		List<RuntimeConstraint> runtimeConstraints = new ArrayList<>();
		for(Constraint c : getConstraints()){
			if(!c.isCategory(AssignmentTimeVar.class) && !c.isCategory(TimeInSignature.class))
				continue;
			for(RuntimeConstraint r : c.getRuntimeConstraints()){
				if(!runtimeConstraints.contains(r))
					runtimeConstraints.add(r);
			}
		}
		runtimeConstraints.sort(Comparator.comparing(RuntimeConstraint::getClassName).thenComparing(RuntimeConstraint::getLine));
		return runtimeConstraints;
	}
}
