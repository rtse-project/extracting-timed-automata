package parser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.io.FileUtils.readFileToString;


/**
 * This class handle the opening of a file and its parsing.
 *
 * @author      Giovanni Liva (@thisthatDC)
 * @version     %I%, %G%
 */

public class Java2AST {

	private static Map<String, List<String>> cacheClassPathProject = new HashMap<>();

    private String filename;
	private String projectPath = "";
	private List<String> classPath = new ArrayList<>();
    private boolean isParsed = false;

    //lexer&parser
	private ASTParser parserJDT;
	private char[] source;

	/**
	 * Getter of the AST
	 * @return the AST of the source file
	 */
	public CompilationUnit getContextJDT() {
		return contextJDT;
	}

    private CompilationUnit contextJDT;

    /**
     * Constructor that accept the file to parse and a flag.
     * It will initialize the Lexer and Parser.
     * If the parse flag is true, it will parse the file as well.
     * It does <b>not</b> handle any IO Error
     *
     * @param filename      Path of the file to parse
     * @param parse         Boolean value to decide if the file has to be parsed directly
     * @throws IOException  Exception in the case some filesystem problems will arise
     * @throws UnparsableException  Exception in the case some file cannot be parsed by JDT
     */
    public Java2AST(String filename, boolean parse) throws IOException, UnparsableException {
        this.filename = filename;
        initParser();
        if(parse){
            convertToAST();
        }
    }

	public Java2AST(String filename, boolean parse, String projectPath) throws IOException, UnparsableException {
		this.filename = filename;
		this.projectPath = projectPath;
		this.classPath = compute();
		initParser();
		if(parse){
			convertToAST();
		}
	}

	private List<String> compute() {
    	if(cacheClassPathProject.containsKey(this.projectPath))
    		return cacheClassPathProject.get(this.projectPath);

    	List<String> out = new ArrayList<>();

		Collection<File> dirs = FileUtils.listFilesAndDirs(new File(this.projectPath), TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);

		for(File s : dirs){
			if(s.toString().endsWith("src/main/java")){
				out.add(s.toString());
			}
		}
		out.add(System.getProperty("java.home") + "/lib");
		cacheClassPathProject.put(this.projectPath, out);
		return out;
	}

	public Java2AST(String filename, boolean parse, String projectPath, List<String> classPath) throws IOException, UnparsableException {
		this.filename = filename;
		this.projectPath = projectPath;
		this.classPath = classPath;
		initParser();
		if(parse){
			convertToAST();
		}
	}


    /**
     * Initialization of Lexer & Parser
     * If something goes wrong, it will throw an IO Exception
     *
     * @throws IOException
     */
    public void initParser() throws IOException {

		String[] sources = new String[]{ this.projectPath };
		String[] classPath = this.classPath.toArray(new String[0]);
//				new String[]{ System.getProperty("java.home") + "/lib"};

		File file1 = new File(this.filename);
		String source = readFileToString(file1, "utf-8");

		parserJDT = ASTParser.newParser(AST.JLS8);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8
		parserJDT.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parserJDT.setCompilerOptions(options);

		//if(!this.projectPath.equals(""))
		parserJDT.setEnvironment(classPath, sources, new String[]{"UTF-8"}, true);

		parserJDT.setResolveBindings(true);
		parserJDT.setBindingsRecovery(true);

		parserJDT.setUnitName(filename.substring(filename.lastIndexOf("/")+1));

		this.source = source.toCharArray();
		ASTSrc.getInstance().setSource(this.source);

		parserJDT.setSource(source.toCharArray());
    }


	/**
	 * It converts the java source file into the AST representation.
	 * @throws UnparsableException
	 */
	public void convertToAST() throws UnparsableException {
    	try {
			contextJDT = (CompilationUnit) parserJDT.createAST(null);
		} catch (Exception e){
			throw new UnparsableException(this.filename);
		}
        isParsed = true;
		ASTSrc.getInstance().setJDT(contextJDT);
    }

	public void setClassPath(List<String> classPath) {
		this.classPath = classPath;
	}

	public char[] getSource() {
		return source;
	}

	public void dispose() {
		System.gc();
	}
}
