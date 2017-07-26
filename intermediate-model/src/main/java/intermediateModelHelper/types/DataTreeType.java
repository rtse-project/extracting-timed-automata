package intermediateModelHelper.types;

import intermediateModel.structure.ASTClass;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.mongoConnector.MongoOptions;
import intermediateModelHelper.indexing.structure.IndexData;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class DataTreeType {
	DataTreeType extended = null;
	IndexData current;
	List<DataTreeType> interfaces = new ArrayList<>();

	private static Map<Pair<String,String>,Triplet<DataTreeType,IndexData,List<DataTreeType>>> cache = new HashMap<>();

	public DataTreeType(ASTClass _class) {
		MongoOptions options = MongoOptions.getInstance();
		MongoConnector db = MongoConnector.getInstance( options.getDbName() );
		Pair<String,String> p = new Pair<>(_class.getPackageName(), _class.getName());
		if(cache.containsKey(p)){
			Triplet<DataTreeType,IndexData,List<DataTreeType>> t = cache.get(p);
			extended = t.getValue0();
			current = t.getValue1();
			interfaces = t.getValue2();
		} else {
			current = db.getIndex(_class).get(0);
			solveBinding(db);
			Triplet<DataTreeType,IndexData,List<DataTreeType>> t = new Triplet<>(extended, current, interfaces);
			cache.put(p, t);
		}

	}

	public DataTreeType(String _class, String _package) {
		MongoOptions options = MongoOptions.getInstance();
		MongoConnector db = MongoConnector.getInstance( options.getDbName());
		Pair<String,String> p = new Pair<>(_package, _class);
		if(cache.containsKey(p)){
			Triplet<DataTreeType,IndexData,List<DataTreeType>> t = cache.get(p);
			extended = t.getValue0();
			current = t.getValue1();
			interfaces = t.getValue2();
		} else {
			current = db.getIndex(_class, _package).get(0);
			solveBinding(db);
			Triplet<DataTreeType,IndexData,List<DataTreeType>> t = new Triplet<>(extended, current, interfaces);
			cache.put(p, t);
		}
	}

	private void solveBinding(MongoConnector db) {
		String extType = current.getExtendedType();
		/* if(extType.equals("Object")){
			return;
		} */
		for(String i : current.getImports()){
			List<IndexData> imports = db.getFromImport(i);
			for(IndexData index : imports){
				//Check on types to resolve the extends
				if(index.getClassName().equals(extType)){
					try {
						extended = new DataTreeType(index.getName(), index.getClassPackage());
					} catch (Exception e){
						//we cannot resolve the binding for the extended
						extended = null;
					}
				}
				//Check on interfaces
				if( current.getInterfacesImplemented().stream().anyMatch( tInterface -> tInterface.equals(index.getName())) ){
					try {
						DataTreeType tmp = new DataTreeType(index.getName(), index.getClassPackage());
						interfaces.add(tmp);
					} catch (Exception e){
						//we cannot resolve the binding for the interface
					}
				}
			}
		}
		List<IndexData> imports = db.getFromImport(current.getClassPackage() + ".*");
		for(IndexData index : imports){
			//Check on types to resolve the extends
			if(index.getClassName().equals(extType)){
				try {
					extended = new DataTreeType(index.getName(), index.getClassPackage());
				} catch (Exception e){
					//we cannot resolve the binding for the extended
					extended = null;
				}
			}
			//Check on interfaces
			if( current.getInterfacesImplemented().stream().anyMatch( tInterface -> tInterface.equals(index.getName())) ){
				try {
					DataTreeType tmp = new DataTreeType(index.getName(), index.getClassPackage());
					interfaces.add(tmp);
				} catch (Exception e){
					//we cannot resolve the binding for the interface
				}
			}
		}
	}


	public boolean isTypeCompatible(String type){
		if(current.getClassName().equals(type) || current.getExtendedType().equals(type)){ //extended or current type
			return true;
		}
		//interfaces
		boolean flag = false;
		for(DataTreeType _interface : interfaces){
			if(_interface.isTypeCompatible(type)){
				flag = true;
			}
		}
		if(flag){
			return true;
		}
		if(extended == null) {
			return false;
		}
		return extended.isTypeCompatible(type);
	}

	public boolean isExtending(String _className){
		if(current.getExtendedType().equals(_className)){
			return true;
		}
		if(extended == null) {
			return false;
		}
		return extended.isExtending(_className);
	}

	public static boolean checkCompatibleTypes(String type1, String type2, String pkg1, String pkg2){
		if(type1 == null || type2 == null){
			return false;
		}
		//some one extends the other?
		try {
			DataTreeType t1 = new DataTreeType(type1, pkg1);
			if (t1.isTypeCompatible(type2)) {
				return true;
			}
		} catch (Exception e){
			//we don't have to do anything
		}
		/* compatible means that type1 < type2 and not viceversa
		try {
			DataTreeType t2 = new DataTreeType(type2, pkg2);
			if (t2.isTypeCompatible(type1)) {
				return true;
			}
		} catch (Exception e){
			//we don't have to do anything
		}*/
		return checkEqualsTypes(type1,type2);
	}

	public static boolean checkEqualsTypes(String type1, String type2, String pkg1, String pkg2){
		if(type1 == null || type2 == null){
			return false;
		}
		//some one extends the other?
		try {
			DataTreeType t1 = new DataTreeType(type1, pkg1);
			if (t1.isTypeCompatible(type2)) {
				return true;
			}
		} catch (Exception e){
			//we don't have to do anything
			//System.err.println(e.getMessage());
		}
		try{
			DataTreeType t2 = new DataTreeType(type2, pkg2);
			if (t2.isTypeCompatible(type1)) {
				return true;
			}
		} catch (Exception e){
			//we don't have to do anything
			//System.err.println(e.getMessage());
		}
		return checkEqualsTypes(type1,type2);
	}

	private static boolean checkEqualsTypes(String type1, String type2) {
		//search for basic types
		List<String> basic_int = new ArrayList<>(Arrays.asList(new String[]{"short", "int", "long", "float", "double", "Short", "Integer", "Long", "Float", "Double"}));
		List<String> basic_bool = new ArrayList<>(Arrays.asList(new String[]{"boolean","Boolean"}));
		if(basic_int.contains(type1) && basic_int.contains(type2)) return true;
		if(basic_bool.contains(type1) && basic_bool.contains(type2)) return true;
		//dots?
		type1 = type1.contains("<") ? type1.substring(0, type1.indexOf("<")) : type1;
		type2 = type2.contains("<") ? type2.substring(0, type2.indexOf("<")) : type2;
		//arrays?
		type1 = type1.contains("[") ? type1.substring(0, type1.indexOf("[")) : type1;
		type2 = type2.contains("[") ? type2.substring(0, type2.indexOf("[")) : type2;
		String t1 = type1.contains(".") ? type1.substring(type1.indexOf(".") +1 ) : type1;
		String t2 = type2.contains(".") ? type2.substring(type2.indexOf(".") +1 ) : type2;
		if(t1.equals("Object") && t2.length() > 1) return Character.isUpperCase(t2.charAt(0));
		if(t2.equals("Object") && t1.length() > 1) return Character.isUpperCase(t1.charAt(0));
		if(t1.equals("Class") && t2.length() > 1) return Character.isUpperCase(t2.charAt(0));
		if(t2.equals("Class") && t1.length() > 1) return Character.isUpperCase(t1.charAt(0));
		//null is always compatible
		if(type1.equals("null") && Character.isUpperCase(type2.charAt(0))) return true;
		if(type2.equals("null") && Character.isUpperCase(type1.charAt(0))) return true;
		//this is ok only when there there are objects
		if(type1.equals("this") && Character.isUpperCase(type2.charAt(0))) return true;
		if(type2.equals("this") && Character.isUpperCase(type1.charAt(0))) return true;
		return t1.equals(t2);
	}

}
