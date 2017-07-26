package intermediateModelHelper.envirorment;


import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.indexing.mongoConnector.MongoConnector;
import intermediateModelHelper.indexing.structure.IndexData;
import intermediateModelHelper.types.ResolveTypes;

import java.util.List;

/**
 * The class handle kinda of a symbol table for dynamic scoping.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class EnvExtended extends Env {

	String className;
	String packageName;

	/**
	 * We create an Environment that is the first one
	 */
	protected EnvExtended(String className, String packageName) {
		super(null);
		this.className = className;
		this.packageName = packageName;
	}

	protected EnvExtended(Env prev, String className, String packageName) {
		super(prev);
		this.className = className;
		this.packageName = packageName;
	}

	/**
	 * Create the Object on the top of the previous Env
	 * @param prev	Previous environment
	 */
	protected EnvExtended(Env prev) {
		super(prev);
	}

	public static EnvExtended getExtendedEnv(ASTClass c){
		if(c.getExtendClass().equals("Object")){
			return new EnvExtended("Object","");
		}
		MongoConnector mongo = MongoConnector.getInstance();
		IndexData parent = ResolveTypes.getPackageFromImportsString(c.getRealPackageName(), c.getImportsAsString(), c.getExtendClass());
		if(parent != null){
			List<ASTClass> classes = JDTVisitor.parse(parent.getPath(),parent.getPath());
			for(ASTClass p : classes){
				if(p.getName().equals(c.getExtendClass())){
					EnvExtended out = new EnvExtended( getExtendedEnv(p), p.getName(), p.getPackageName());
					BuildEnvironment.getInstance().buildEnvClass(p, out);
					return out;
				}
			}
		}
		return new EnvExtended("Object","");
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}



}
