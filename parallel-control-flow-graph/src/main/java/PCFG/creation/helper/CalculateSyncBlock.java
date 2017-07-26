package PCFG.creation.helper;

import PCFG.structure.PCFG;
import PCFG.structure.edge.SyncEdge;
import PCFG.structure.node.SyncNode;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModelHelper.indexing.structure.IndexParameter;
import intermediateModelHelper.indexing.structure.IndexSyncBlock;
import intermediateModelHelper.types.DataTreeType;
import org.javatuples.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class CalculateSyncBlock {

	public static void calculateSyncBlock(List<KeyValue<IASTMethod,ASTClass>> classes, List<KeyValue<KeyValue<IASTMethod,ASTClass>, List<IndexSyncBlock>>> indexes, PCFG pcfg){
		if(classes.size() < 2) return;
		if(classes.size() == 2){
			calculateSyncBlockDifferentClasses(classes.get(0), classes.get(1), indexes, pcfg);
		} else {
			for(KeyValue<IASTMethod,ASTClass> c1 : classes){
				for(KeyValue<IASTMethod,ASTClass> c2 : classes) {
					if(c1 != c2)
						calculateSyncBlockDifferentClasses(c1, c2, indexes, pcfg);
				}
			}
		}
	}

	private static void calculateSyncBlockDifferentClasses(KeyValue<IASTMethod,ASTClass> class_1, KeyValue<IASTMethod,ASTClass> class_2, List<KeyValue<KeyValue<IASTMethod,ASTClass>,List<IndexSyncBlock>>> indexes, PCFG pcfg) {
		//get the sync blocks of the methods that we are looking for
		ArrayList<KeyValue<IASTMethod,ASTClass>> classes = new ArrayList<>();
		classes.add(class_1);
		classes.add(class_2);
		List<IndexSyncBlock> syncBlocks_1 = getSyncBlocks(class_1, indexes);
		List<IndexSyncBlock> syncBlocks_2 = getSyncBlocks(class_2, indexes);
		boolean isSameClass = class_1.getValue().isSameClass(class_2.getValue()) && class_1.getValue().getPackageName().equals(class_2.getValue().getPackageName());
		//everyone is checked against everyone
		for(IndexSyncBlock outter : syncBlocks_1){
			IndexParameter varOutter = outter.getSyncVar(); //outter.getEnv().getVar(outter.getExpr());
			if(varOutter == null || varOutter.getType() == null) { //could be a method call or something else than a simple var :(
				//for the moment we consider only variables
				continue;
			}
			for(IndexSyncBlock inner : syncBlocks_2) {
				IndexParameter varInner = inner.getSyncVar(); //inner.getEnv().getVar(inner.getExpr());
				if(varInner == null && isSameClass){
					if(inner.getExpr().equals(outter.getExpr())){
						//we have a match on this or ClassName.class
						processMatch(pcfg, outter, inner, classes, isSameClass);
					}
				} else if(varInner != null) {
					boolean canWeCheck = 	(isSameClass && inner.getExpr().equals(outter.getExpr())) || //same class on same var)
							(inner.isInherited() && outter.isInherited() && inner.sameInheritance(outter)) || //both vars can be readed from outside
							(inner.isAccessibleWritingFromOutside() && outter.getExpr().equals("this")) || //the first is accessible writing from outside and the other is this
							(outter.isAccessibleWritingFromOutside() && inner.getExpr().equals("this")) || //the outter is accessible writing from outside and the inner is this
							(outter.isAccessibleWritingFromOutside() && inner.isAccessibleWritingFromOutside()) ; //both can share a variable from outside
					if(
							canWeCheck //both are accessible from outside or we are checking on same classes
									&&
									DataTreeType.checkEqualsTypes(inner.getExprType(), outter.getExprType(), inner.getExprPkg(), outter.getExprPkg())){
						processMatch(pcfg, outter, inner, classes, isSameClass);
					}
				}
			}
		}
	}

	private static void processMatch(PCFG pcfg, IndexSyncBlock outter, IndexSyncBlock inner, List<KeyValue<IASTMethod,ASTClass>> classes, boolean isSameClass) {
		//we gotta a match
		SyncNode outSync = pcfg.getSyncNodeByExpr( outter.getExpr(), outter.getStart(), outter.getEnd(), outter.getLine(), classes.get(0).getValue().hashCode() );
		SyncNode inSync  = pcfg.getSyncNodeByExpr( inner.getExpr(), inner.getStart(), inner.getEnd(), inner.getLine(), classes.get(1).getValue().hashCode() );
		if(isSameClass){
			if (outSync == inSync && outter.getStart() != inner.getStart()) { //the case where we have same class 2+ times
				inSync = pcfg.getSyncNodeByExprSkip(inner.getExpr(), inner.getStart(), inner.getEnd(), inner.getLine(), 1);
			}
		} else {
			if (outSync == inSync) { //the case where we have same class 2+ times
				inSync = pcfg.getSyncNodeByExprSkip(inner.getExpr(), inner.getStart(), inner.getEnd(), inner.getLine(), 1);
			}
		}
		SyncEdge sEdge = null;
		if(outSync == null || inSync == null){
			// we have smt in static init -> How to handle?
			System.err.println("Null pointer to sync block");
			for(KeyValue<IASTMethod,ASTClass> c : classes){
				System.err.println(c.getKey()  + " :: " + c.getValue().getPackageName() + "." + c.getValue().getName());
			}
			System.err.println("_____");
		} else {
			//link between two different sync blocks
			try {
				sEdge = new SyncEdge(outSync, inSync);
				pcfg.addSyncEdge(sEdge);
			} catch (SyncEdge.MalformedSyncEdge malformedSyncEdge) {
				malformedSyncEdge.printStackTrace();
			}
		}
	}


	private static List<IndexSyncBlock> getSyncBlocks(KeyValue<IASTMethod,ASTClass> c, List<KeyValue<KeyValue<IASTMethod,ASTClass>, List<IndexSyncBlock>>>indexes){
		List<IndexSyncBlock> syncBlocks = new ArrayList<>();
		List<IndexSyncBlock> ss = new ArrayList<>();
		for(KeyValue<KeyValue<IASTMethod,ASTClass>, List<IndexSyncBlock>> single : indexes){
			KeyValue<IASTMethod,ASTClass> k = single.getKey();
			if(k.equals(c)){
				return single.getValue();
			}
		}
		return (syncBlocks);
	}

	private static List<IndexSyncBlock> removeDuplicates(List<IndexSyncBlock> tmp_syncBlocks){
		List<IndexSyncBlock> syncBlocks = new ArrayList<>();
		for(IndexSyncBlock is : tmp_syncBlocks){
			boolean exists = false;
			for(IndexSyncBlock actualSync : syncBlocks){
				if(actualSync.equals(is)){
					exists = true;
				}
			}
			if(!exists){
				syncBlocks.add(is);
			}
		}
		return syncBlocks;
	}

	private static boolean sameClass(IndexSyncBlock inner, IndexSyncBlock outter) {
		return inner.getPackageName().equals(outter.getPackageName()) && inner.getName().equals(outter.getName());
	}

}
