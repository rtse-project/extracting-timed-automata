package server.handler.outputFormat;

import intermediateModelHelper.indexing.structure.IndexData;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class OutputData {
	String path;
	String className;
	String packageName;

	public OutputData(String path, String className, String packageName) {
		this.path = path;
		this.className = className;
		this.packageName = packageName;
	}

	public OutputData(IndexData d, String base_path) {
		this.path = d.getPath().replace(base_path,"");
		this.className = d.getClassName();
		this.packageName = d.getFullclassPackage();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
