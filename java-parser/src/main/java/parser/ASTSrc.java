package parser;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class ASTSrc {
	private static ASTSrc instance = null;
	public char[] source = {};
	public CompilationUnit cu;

	protected ASTSrc() {
		// Exists only to defeat instantiation.
	}
	public static synchronized ASTSrc getInstance() {
		if(instance == null) {
			instance = new ASTSrc();
		}
		return instance;
	}
	public synchronized void setSource(char[] s){
		source = s;
	}
	public synchronized void setJDT(CompilationUnit cu){
		this.cu = cu;
	}

	public int getLine(int start){
		return cu.getLineNumber(start) - 1;
	}

}
