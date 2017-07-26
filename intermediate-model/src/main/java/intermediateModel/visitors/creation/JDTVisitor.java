package intermediateModel.visitors.creation;

import intermediateModel.interfaces.IASTHasStms;
import intermediateModel.interfaces.IASTMethod;
import intermediateModel.interfaces.IASTRE;
import intermediateModel.structure.*;
import intermediateModel.structure.expression.*;
import intermediateModel.visitors.creation.utility.Getter;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import parser.Java2AST;
import parser.UnparsableException;

import java.io.IOException;
import java.util.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class JDTVisitor extends ASTVisitor {

	public List<ASTClass> listOfClasses = new ArrayList<ASTClass>();
	private CompilationUnit cu;
	private String packageName = "";
	private List<ASTImport> listOfImports = new ArrayList<>();
	private ASTClass lastClass;
	private IASTHasStms lastMethod = new ASTMethod(0,0, ASTMethod.lambda, "void", new ArrayList<>(), new ArrayList<>(), false, false, false);

	private Stack<ASTClass> stackClasses = new Stack<>();
	private Stack<String> stackPackage = new Stack<>();
	private Stack<IASTHasStms> stackSwitch = new Stack<>();
	private Stack<ASTSwitch> casewitch = new Stack<>();
	private String path;
	private String lastLabel = null;

	private static Map<String, List<ASTClass>> cache = new HashMap<>();

	public static List<ASTClass> parse(String filename){
		return parse(filename, filename.substring(0, filename.lastIndexOf("/")));
	}

	public static List<ASTClass> parse(String filename, String projectPath) {
		if(cache.containsKey(filename)){
			return cache.get(filename);
		}
		Java2AST a = null;
		try {
			a = new Java2AST(filename, true, projectPath);
		}
		catch (IOException e) {}
		catch (UnparsableException e) {
			//cannot parse the file
			return new ArrayList<>();
		}
		CompilationUnit result = a.getContextJDT();
		a.dispose();
		JDTVisitor v = new JDTVisitor(result, filename);
		result.accept(v);
		cache.put(filename, v.listOfClasses);
		return v.listOfClasses;
	}

	public static List<ASTClass> parseSpecial(String filename, String projectPath) {
		if(cache.containsKey(filename)){
			return cache.get(filename);
		}
		Java2AST a = null;
		try {
			a = new Java2AST(filename, false, projectPath);
			a.setClassPath(new ArrayList<>());
			a.initParser();
			a.convertToAST();
		}
		catch (IOException e) {}
		catch (UnparsableException e) {
			//cannot parse the file
			return new ArrayList<>();
		}
		CompilationUnit result = a.getContextJDT();
		JDTVisitor v = new JDTVisitor(result, filename);
		result.accept(v);
		cache.put(filename, v.listOfClasses);
		return v.listOfClasses;
	}

	public JDTVisitor(CompilationUnit cu, String path) {
		this.cu = cu;
		this.path = path;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		packageName = node.getName().getFullyQualifiedName();
		stackPackage.add(packageName);
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		String suffix = "";
		if(node.toString().trim().endsWith("*;")){
			suffix = ".*";
		}
		listOfImports.add( new ASTImport(start, stop, false, node.getName().getFullyQualifiedName() + suffix ));
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		//class generation
		String className = node.getName().getFullyQualifiedName();
		ASTClass.Visibility visibility = ASTClass.Visibility.PRIVATE;
		if(node.modifiers().size() > 0){
			int i = 0;
			while(i < node.modifiers().size() && !(node.modifiers().get(i) instanceof Modifier)){
				i++;
			}
			if(i != node.modifiers().size()) {
				Modifier m = (Modifier) node.modifiers().get(i);
				visibility = Getter.visibility(m.toString());
			}
		}
		String superClass = node.getSuperclassType() == null ? "Object" : node.getSuperclassType().toString();
		List<String> superInterfaces = new ArrayList<>();
		for(Object ost : node.superInterfaceTypes()){
			if(ost instanceof SimpleType) {
				SimpleType st = (SimpleType) ost;
				superInterfaces.add(st.getName().getFullyQualifiedName());
			}
			else {
				ParameterizedType pt = (ParameterizedType) ost;
				superInterfaces.add(pt.getType().toString());
			}
		}
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTClass c = new ASTClass(start, stop, packageName, className, visibility, superClass, superInterfaces);
		c.setPath(path);
		c.setImports(listOfImports);
		//attributes of the class
		for(FieldDeclaration f : node.getFields()){
			ASTClass.Visibility vis = ASTClass.Visibility.PRIVATE;
			if(f.modifiers().size() > 0){
				int i = 0;
				while(i < f.modifiers().size() && !(f.modifiers().get(i) instanceof Modifier)){
					i++;
				}
				if(i < f.modifiers().size()) {
					Modifier m = (Modifier) f.modifiers().get(i);
					vis = Getter.visibility(m.toString());
				}
				boolean isAbs = false;
				for(i = 0; i < f.modifiers().size(); i++){
					if(f.modifiers().get(i) instanceof Modifier){
						Modifier m = (Modifier) f.modifiers().get(i);
						if(m.isAbstract()) isAbs = true;
					}
				}
				c.setAbstract(isAbs);
			}
			String type = f.getType().toString();
			String name = "";
			ASTRE expr = null;
			for(Object ovf : f.fragments()){
				VariableDeclarationFragment vf = (VariableDeclarationFragment) ovf;
				name = vf.getName().getFullyQualifiedName();
				expr = getExprState(vf.getInitializer());
				int ss = 0; int st = 0;
				ss = f.getStartPosition();
				st = ss + f.getLength();
				ASTAttribute attribute = new ASTAttribute(ss, st, vis, type, name, expr);
				String pkg, nmm;
				ITypeBinding typePointed = f.getType().resolveBinding();
				if(typePointed != null){
					pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
					nmm = typePointed.getName();
					String classPointed = pkg + "." + nmm;
					if(classPointed.startsWith(".")){
						classPointed = classPointed.substring(1);
					}
					attribute.setTypePointed(classPointed);
					//System.out.println(node.toString() + " points to " + classPointed);
				}
				c.addAttribute(attribute);
				if(vf.getInitializer() instanceof LambdaExpression ) vf.getInitializer().delete(); //avoid lambda interfeer with remaning
			}
		}
		c.setInterface(node.isInterface());
		packageName = packageName + "." + className;
		stackPackage.push(packageName);

		c.setParent(stackClasses.size() > 0 ? stackClasses.peek() : null);

		listOfClasses.add(c);
		stackClasses.push(c);
		lastClass = c;
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		String className = node.getName().getFullyQualifiedName();
		ASTClass.Visibility visibility = ASTClass.Visibility.PRIVATE;
		if(node.modifiers().size() > 0){
			int i = 0;
			while(i < node.modifiers().size() && !(node.modifiers().get(i) instanceof Modifier)){
				i++;
			}
			if(i != node.modifiers().size()) {
				Modifier m = (Modifier) node.modifiers().get(i);
				visibility = Getter.visibility(m.toString());
			}
		}
		String superClass = "Object";
		List<String> superInterfaces = new ArrayList<>();
		for(Object ost : node.superInterfaceTypes()){
			if(ost instanceof SimpleType) {
				SimpleType st = (SimpleType) ost;
				superInterfaces.add(st.getName().getFullyQualifiedName());
			}
			else {
				ParameterizedType pt = (ParameterizedType) ost;
				superInterfaces.add(pt.getType().toString());
			}
		}
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTClass c = new ASTClass(start, stop, packageName, className, visibility, superClass, superInterfaces);
		c.setPath(path);
		c.setImports(listOfImports);
		//attributes of the class
		for(Object fo : node.bodyDeclarations()){
			if(!(fo instanceof FieldDeclaration)){
				continue;
			}
			FieldDeclaration f = (FieldDeclaration) fo;
			ASTClass.Visibility vis = ASTClass.Visibility.PRIVATE;
			if(f.modifiers().size() > 0){
				int i = 0;
				while(i < f.modifiers().size() && !(f.modifiers().get(i) instanceof Modifier)){
					i++;
				}
				if(i < f.modifiers().size()) {
					Modifier m = (Modifier) f.modifiers().get(i);
					vis = Getter.visibility(m.toString());
				}
			}
			String type = f.getType().toString();
			String name = "";
			ASTRE expr = null;
			for(Object ovf : f.fragments()){
				VariableDeclarationFragment vf = (VariableDeclarationFragment) ovf;
				name = vf.getName().getFullyQualifiedName();
				expr = getExprState(vf.getInitializer());
			}
			int ss = 0; int st = 0;
			ss = f.getStartPosition();
			st = ss + f.getLength();
			ASTAttribute attribute = new ASTAttribute(ss, st, vis, type, name, expr);
			String pkg, nmm;
			ITypeBinding typePointed = f.getType().resolveBinding();
			if(typePointed != null){
				pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
				nmm = typePointed.getName();
				String classPointed = pkg + "." + nmm;
				if(classPointed.startsWith(".")){
					classPointed = classPointed.substring(1);
				}
				attribute.setTypePointed(classPointed);
				//System.out.println(node.toString() + " points to " + classPointed);
			}
			c.addAttribute(attribute);
		}
		//const of enum
		for(Object co : node.enumConstants()){
			if(!(co instanceof  EnumConstantDeclaration)) continue;
			EnumConstantDeclaration cons = (EnumConstantDeclaration) co;
			String type = c.getName();
			String name = cons.getName().getFullyQualifiedName();
			int ss = 0; int st = 0;
			ss = cons.getStartPosition();
			st = ss + cons.getLength();
			ASTRE expr = null;

			ASTAttribute attribute = new ASTAttribute(ss, st, ASTClass.Visibility.PUBLIC, type, name, expr);
			String pkg, nmm;
			ITypeBinding typePointed = cons.resolveVariable() != null ? cons.resolveVariable().getType() : null;
			if(typePointed != null){
				pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
				nmm = typePointed.getName();
				String classPointed = pkg + "." + nmm;
				if(classPointed.startsWith(".")){
					classPointed = classPointed.substring(1);
				}
				attribute.setTypePointed(classPointed);
				//System.out.println(node.toString() + " points to " + classPointed);
			}
			c.addAttribute(attribute);
		}
		packageName = packageName + "." + className;
		stackPackage.push(packageName);

		c.setParent(stackClasses.size() > 0 ? stackClasses.peek() : null);

		listOfClasses.add(c);
		stackClasses.push(c);
		lastClass = c;
		return true;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		if(stackClasses.size() > 0) {
			ASTClass c = stackClasses.pop();
			if (c.equals(lastClass) && stackClasses.size() > 0) {
				lastClass = stackClasses.peek();
			}
		}
		if(stackPackage.size() > 0) {
			String t = stackPackage.pop();
			if (t.equals(packageName) && stackPackage.size() > 0) {
				packageName = stackPackage.peek();
			}

		}
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		if(stackClasses.size() > 0) {
			ASTClass c = stackClasses.pop();
			if (c.equals(lastClass) && stackClasses.size() > 0) {
				lastClass = stackClasses.peek();
			}
		}
		if(stackPackage.size() > 0) {
			String t = stackPackage.pop();
			if (t.equals(packageName) && stackPackage.size() > 0) {
				packageName = stackPackage.peek();
			}

		}
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		if(stackClasses.size() > 0) {
			ASTClass c = stackClasses.pop();
			if (c.equals(lastClass) && stackClasses.size() > 0) {
				lastClass = stackClasses.peek();
			}
		}
		//test

	}

	@Override
	public boolean visit(Initializer node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTStatic s = new ASTStatic(start, stop);
		lastClass.addStaticInit(s);
		lastMethod = s;
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		String methodName = node.getName().getFullyQualifiedName();
		String returnType = node.getReturnType2() == null ? null : node.getReturnType2().toString();
		List<ASTVariable> pars = new ArrayList<ASTVariable>();
		List<String> throwedException = new ArrayList<String>();
		//throws
		for(Object oth : node.thrownExceptionTypes()){
			SimpleType th = (SimpleType) oth;
			throwedException.add(th.getName().getFullyQualifiedName());
		}
		//pars
		for(Object op : node.parameters()){
			SingleVariableDeclaration p = (SingleVariableDeclaration) op;
			int ss, st;
			ss = p.getStartPosition();
			st = ss + p.getLength();
			ASTVariable par = new ASTVariable(ss, st, p.getName().getFullyQualifiedName(), p.getType().toString());
			//par.setAnnotations( AnnotationVisitor.getAnnotationVariable(p.modifiers(), p) );
			ITypeBinding typePointed =  p.getType().resolveBinding();
			if(typePointed != null){
				String pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
				String nmm = typePointed.getName();
				String classPointed = pkg + "." + nmm;
				if(classPointed.startsWith(".")){
					classPointed = classPointed.substring(1);
				}
				par.setTypePointed(classPointed);
				//System.out.println(node.toString() + " points to " + classPointed);
			}
			pars.add(par);
		}
		//is syncronized
		boolean isSync = false;
		boolean isAbs = false;
		boolean isStatic = false;
		for(Object m : node.modifiers()){
			if(m instanceof Modifier){
				Modifier modifier = (Modifier)m;
				if(modifier.isSynchronized()){
					isSync = true;
				}
				if(modifier.isAbstract()){
					isAbs = true;
				}
				if(modifier.isStatic()){
					isStatic = true;
				}
			}
		}

		IASTMethod method = null;
		if(returnType == null){
			//constructor
			method = new ASTConstructor(start, stop, methodName, pars, throwedException);
		} else {
			method = new ASTMethod(start, stop, methodName, returnType, pars, throwedException, isSync, isAbs, isStatic);
		}
		lastClass.addMethod(method);
		lastMethod = method;
		//stackMethods.push(method);
		return true;
	}


	@Override
	public boolean visit(ForStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTFor forstm = new ASTFor(start, stop);

		if(this.lastLabel != null){
			forstm.setIdentifier(this.lastLabel);
			this.lastLabel = null;
		}

		lastMethod.addStms(forstm);
		lastMethod = forstm;
		//init expr
		if(node.initializers().size() > 0){
			List<ASTRE> init = new ArrayList<>();
			for(Object oInit : node.initializers()){
				ASTNode expr = (ASTNode) oInit;
				ASTRE initExpr = getExprState(expr);
				init.add(initExpr);
			}
			forstm.setInit(init);
		}
		//guard expr
		if(node.getExpression() != null){
			ASTRE guard = getExprState(node.getExpression());
			forstm.setExpr(guard);
		}
		//update expr
		if(node.updaters().size() > 0){
			List<ASTRE> update = new ArrayList<>();
			for(Object oInit : node.updaters()){
				ASTNode expr = (ASTNode) oInit;
				ASTRE updateExpr = getExprState(expr);
				update.add(updateExpr);
			}
			forstm.setPost(update);
		}
		node.getBody().accept(this);
		node.setBody(createEmpty());
		lastMethod = bck;
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();

		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}

		SingleVariableDeclaration v = node.getParameter();
		int vstart = v.getStartPosition();
		int vstop = vstart + v.getLength();
		String vname = v.getName().getIdentifier();
		String vtype = v.getType().toString();
		ASTVariable var = new ASTVariable(vstart, vstop, vname, vtype);
		ITypeBinding typePointed =  v.getType().resolveBinding();
		if(typePointed != null){
			String pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
			String nmm = typePointed.getName();
			String classPointed = pkg + "." + nmm;
			if(classPointed.startsWith(".")){
				classPointed = classPointed.substring(1);
			}
			var.setTypePointed(classPointed);
			//System.out.println(node.toString() + " points to " + classPointed);
		}
		ASTRE expr = getExprState(node.getExpression());

		ASTForEach foreach = new ASTForEach(start,stop, var, expr);
		if(stmLabel != null){
			foreach.setIdentifier(stmLabel);
		}
		lastMethod.addStms(foreach);
		lastMethod = foreach;

		node.getBody().accept(this);
		node.setBody(createEmpty());

		lastMethod = bck;
		return true;
	}

	@Override
	public boolean visit(IfStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();

		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}

		ASTRE guard = getExprState(node.getExpression());
		ASTIf ifstm = new ASTIf(start, stop, guard);
		if(stmLabel != null){
			ifstm.setIdentifier(stmLabel);
		}

		lastMethod.addStms(ifstm);

		int startThen = node.getThenStatement().getStartPosition();
		int stopThen = startThen + node.getThenStatement().getLength();
		ASTIf.ASTIfStms then = ifstm.new ASTIfStms( startThen, stopThen);
		ifstm.setIfBranch(then);
		lastMethod = then;

		node.getThenStatement().accept(this);

		if(node.getElseStatement() != null){
			int startElse = node.getElseStatement().getStartPosition();
			int stopElse = startElse + node.getElseStatement().getLength();
			ASTIf.ASTElseStms elseBranch = ifstm.new ASTElseStms( startElse, stopElse);
			ifstm.setElseBranch(elseBranch);
			lastMethod = elseBranch;
			node.getElseStatement().accept(this);
		}

		node.setThenStatement(createEmpty());
		node.setElseStatement(createEmpty());

		lastMethod = bck;
		return true;
	}

	@Override
	public boolean visit(TryStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();

		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}

		ASTTry elm;
		if(node.resources().size() > 0){
			//try with resources
			List<ASTRE> resources = new ArrayList<>();
			for(Object r : node.resources()){
				VariableDeclarationExpression expr = (VariableDeclarationExpression)  r;
				resources.add( getExprState(expr) );
			}
			elm = new ASTTryResources(start, stop, resources);
		} else {
			elm = new ASTTry(start,stop);
		}
		if(stmLabel != null){
			elm.setIdentifier(stmLabel);
		}
		lastMethod.addStms(elm);

		//try branch
		int tStart, tStop;
		tStart = node.getBody().getStartPosition();
		tStop = tStart + node.getBody().getLength();
		ASTTry.ASTTryBranch tryBranch = elm.new ASTTryBranch(tStart, tStop);
		elm.setTryBranch(tryBranch);
		lastMethod = tryBranch;
		node.getBody().accept(this);

		//catch
		for(Object oC : node.catchClauses()){
			CatchClause c = (CatchClause) oC;
			int cStart, cStop;
			cStart = c.getStartPosition();
			cStop = cStart + c.getLength();

			SingleVariableDeclaration exception = c.getException();
			int eStart, eStop;
			eStart = exception.getStartPosition();
			eStop = eStart + exception.getLength();

			ASTVariable ex = new ASTVariable(eStart, eStop, exception.getName().getFullyQualifiedName(), exception.getType().toString() );
			ITypeBinding typePointed =  exception.getType().resolveBinding();
			if(typePointed != null){
				String pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
				String nmm = typePointed.getName();
				String classPointed = pkg + "." + nmm;
				if(classPointed.startsWith(".")){
					classPointed = classPointed.substring(1);
				}
				ex.setTypePointed(classPointed);
				//System.out.println(node.toString() + " points to " + classPointed);
			}
			ASTTry.ASTCatchBranch catchBranch = elm.new ASTCatchBranch(cStart, cStop, ex);
			elm.addCatchBranch(catchBranch);
			lastMethod = catchBranch;
			c.getBody().accept(this);
			c.setBody(createEmptyBlock());
		}

		//finally
		if(node.getFinally() != null){
			Block endly = node.getFinally();
			int endStart, endStop;
			endStart = endly.getStartPosition();
			endStop = endStart + endly.getLength();
			ASTTry.ASTFinallyBranch endBranch = elm.new ASTFinallyBranch(endStart, endStop);
			elm.setFinallyBranch(endBranch);
			lastMethod = endBranch;
			endly.accept(this);
		}

		node.setBody(createEmptyBlock());
		node.setFinally(createEmptyBlock());

		lastMethod = bck;
		return true;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		this.lastLabel = node.getLabel().getIdentifier();
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}
		ASTRE expr = getExprState(node.getExpression());
		ASTWhile whilestm = new ASTWhile(start, stop, expr);
		if(stmLabel != null){
			whilestm.setIdentifier(stmLabel);
		}
		lastMethod.addStms(whilestm);
		lastMethod = whilestm;

		node.getBody().accept(this);

		node.setBody(createEmpty());
		lastMethod = bck;
		return true;
	}

	//do while

	@Override
	public boolean visit(DoStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}

		ASTRE expr = getExprState(node.getExpression());
		ASTDoWhile whilestm = new ASTDoWhile(start, stop, expr);
		if(stmLabel != null){
			whilestm.setIdentifier(stmLabel);
		}
		lastMethod.addStms(whilestm);
		lastMethod = whilestm;

		node.getBody().accept(this);

		node.setBody(createEmpty());
		lastMethod = bck;
		return true;
	}

	//swhitch

	@Override
	public boolean visit(SwitchStatement node) {
		IASTHasStms bck = lastMethod;
		stackSwitch.push(bck);
		int start = node.getStartPosition();
		int stop = start + node.getLength();

		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}

		ASTRE expr = getExprState(node.getExpression());
		ASTSwitch switchstm = new ASTSwitch(start, stop, expr);
		if(stmLabel != null){
			switchstm.setIdentifier(stmLabel);
		}
		lastMethod.addStms(switchstm);
		casewitch.push(switchstm);

		return true;
	}

	@Override
	public boolean visit(SwitchCase node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		//i am sure it exists otherwise is not a compilable file
		ASTSwitch top = casewitch.peek();
		List<String> listLabels = new ArrayList<>();
		listLabels.add(
				node.getExpression() == null ? "default" : node.getExpression().toString()
		);
		ASTSwitch.ASTCase casestm = top.new ASTCase( start, stop, listLabels );
		top.addCase(casestm);
		lastMethod = casestm;
		return true;
	}

	@Override
	public void endVisit(SwitchStatement node) {
		lastMethod = stackSwitch.pop();
		casewitch.pop();
	}

	//sync


	@Override
	public boolean visit(SynchronizedStatement node) {
		IASTHasStms bck = lastMethod;
		int start = node.getStartPosition();
		int stop = start + node.getLength();

		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}

		ASTRE expr = getExprState(node.getExpression());
		ASTSynchronized sync = new ASTSynchronized(start, stop, expr);
		if(stmLabel != null){
			sync.setIdentifier(stmLabel);
		}
		lastMethod.addStms(sync);
		lastMethod = sync;
		node.getBody().accept(this);
		node.setBody(createEmptyBlock());

		lastMethod = bck;
		return true;
	}


	//special RE
	@Override
	public boolean visit(BreakStatement node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTBreak b;
		if(node.getLabel() != null){
			b = new ASTBreak(start,stop, node.getLabel().getIdentifier());
		} else {
			b = new ASTBreak(start,stop);
		}

		if(this.lastLabel != null){
			b.setIdentifier(this.lastLabel);
			this.lastLabel = null;
		}
		lastMethod.addStms(b);
		return true;
	}
	@Override
	public boolean visit(ContinueStatement node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTContinue c;
		if(node.getLabel() != null){
			c = new ASTContinue(start, stop, node.getLabel().getIdentifier());
		} else {
			c = new ASTContinue(start, stop);
		}
		if(this.lastLabel != null){
			c.setIdentifier(this.lastLabel);
			this.lastLabel = null;
		}

		lastMethod.addStms(c);
		return true;
	}
	@Override
	public boolean visit(ReturnStatement node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		String stmLabel = null;
		if(this.lastLabel != null){
			stmLabel = this.lastLabel;
			this.lastLabel = null;
		}
		ASTReturn r = new ASTReturn(start,stop, getExprState(node.getExpression()));
		if(stmLabel != null){
			r.setIdentifier(stmLabel);
		}
		lastMethod.addStms(r);
		return true;
	}

	//RE expr
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		String type = node.getType().toString();
		IASTHasStms bck = lastMethod;
		for(Object o : node.fragments()){
			if(o instanceof VariableDeclarationFragment){
				VariableDeclarationFragment v = (VariableDeclarationFragment)o;
				ASTVariableDeclaration newVar = new ASTVariableDeclaration(start, stop, type,
						getExpr(v.getName()), getExpr(v.getInitializer()));

				String pkg, nmm;
				ITypeBinding typePointed = node.getType().resolveBinding();
				if(typePointed != null){
					pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
					nmm = typePointed.getName();
					String classPointed = pkg + "." + nmm;
					if(classPointed.startsWith(".")){
						classPointed = classPointed.substring(1);
					}
					newVar.setTypePointed(classPointed);
					//System.out.println(node.toString() + " points to " + classPointed);
				}
				//annotation
				//re.setAnnotations( AnnotationVisitor.getAnnotationVariable(node.modifiers(), v) );
				ASTRE re = new ASTRE(start, stop,
						newVar
				);
				checkType(re, v.getInitializer());
				if(this.lastLabel != null){
					re.setIdentifier(this.lastLabel);
					this.lastLabel = null;
				}
				try {
					bck.addStms(re);
				} catch (Exception e){
					//lambda expression in a attribute definition -> skip
				}
			}
		}
		lastMethod = bck;
		return true;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		IASTHasStms bck = lastMethod;
		ASTRE re = getExprState(node);
		if(this.lastLabel != null){
			re.setIdentifier(this.lastLabel);
			this.lastLabel = null;
		}
		try {
			bck.addStms(re);
		} catch (Exception e){
			//lambda expression in a attribute definition -> skip
		}
		lastMethod = bck;
		return true;
	}


	@Override
	public boolean visit(ThrowStatement node) {
		int start = node.getStartPosition();
		int stop = start + node.getLength();
		ASTThrow _throw = new ASTThrow(start, stop, getExprState(node.getExpression()) );
		if(this.lastLabel != null){
			_throw.setIdentifier(this.lastLabel);
			this.lastLabel = null;
		}
		lastMethod.addStms(_throw);
		return true;
	}

	protected ASTRE getExprState(ASTNode ctx){
		if(ctx == null)
			return null;
		int start = ctx.getStartPosition();
		int stop = start + ctx.getLength();
		ASTRE expr =  new ASTRE(start, stop, getExpr(ctx));
		checkType(expr, ctx);
		if(this.lastLabel != null){
			expr.setIdentifier(this.lastLabel);
			this.lastLabel = null;
		}
		return expr;
	}

	private void checkType(ASTRE expr, ASTNode ctx) {
		//check return type
		Expression e = null;
		if(ctx instanceof ExpressionStatement){
			e = ((ExpressionStatement) ctx).getExpression();
		} else if (ctx instanceof Expression){
			e = (Expression) ctx;
		}
		if(e != null) {
			ITypeBinding type = e.resolveTypeBinding();
			if (type != null) {
				expr.setType(type.getName());
			}
		}
	}

	//Helper to nullify objects
	private Statement createEmpty(){
		ASTRewrite rewriter = ASTRewrite.create(cu.getAST());
		return (Statement) rewriter.createStringPlaceholder("", ASTNode.EMPTY_STATEMENT);
	}
	private Block createEmptyBlock(){
		ASTRewrite rewriter = ASTRewrite.create(cu.getAST());
		return (Block) rewriter.createStringPlaceholder("", ASTNode.BLOCK);
	}

	private void createEmptyExpr(ASTNode node){
		ASTRewrite rewriter = ASTRewrite.create(cu.getAST());
		rewriter.replace(node, null, null);
	}

	public IASTRE getExpr(ASTNode node){
		if(node == null){
			return null;
		}
		if(node instanceof ExpressionStatement){
			return handleExpressionStatement((ExpressionStatement)node);
		} else if(node instanceof VariableDeclarationExpression){
			return variableDeclaration((VariableDeclarationExpression) node);
		} else {
			return handleExpression((Expression) node);
		}

	}

	private IASTRE handleExpressionStatement(ExpressionStatement exprStm){
		return handleExpression(exprStm.getExpression());
	}

	private IASTRE handleExpression(Expression expr){
		if(expr instanceof SimpleName){
			return simpleName((SimpleName) expr);
		} else if(expr instanceof MethodInvocation){
			return methodInvocation((MethodInvocation)expr);
		} else if(expr instanceof PostfixExpression){
			return postFix((PostfixExpression)expr);
		} else if(expr instanceof PrefixExpression){
			return preFix((PrefixExpression)expr);
		} else if(expr instanceof Assignment){
			return assignment((Assignment)expr);
		} else if(expr instanceof StringLiteral){
			return literal((StringLiteral) expr );
		} else if(expr instanceof BooleanLiteral){
			return literal((BooleanLiteral) expr );
		} else if(expr instanceof NumberLiteral){
			return literal((NumberLiteral) expr );
		} else if(expr instanceof ThisExpression){
			return literal((ThisExpression) expr );
		} else if(expr instanceof NullLiteral){
			return literal((NullLiteral) expr );
		} else if(expr instanceof TypeLiteral){
			return literal((TypeLiteral) expr );
		} else if(expr instanceof CharacterLiteral){
			return literal((CharacterLiteral) expr );
		} else if(expr instanceof ClassInstanceCreation){
			return newObject((ClassInstanceCreation)expr);
		} else if(expr instanceof InfixExpression){
			return mathExpression((InfixExpression) expr);
		} else if(expr instanceof FieldAccess){
			return fieldAccess((FieldAccess) expr);
		} else if(expr instanceof QualifiedName){
			return fieldAccess((QualifiedName) expr);
		} else if(expr instanceof VariableDeclarationExpression) {
			return variableDeclaration((VariableDeclarationExpression)expr);
		} else if(expr instanceof ParenthesizedExpression){
			return getExpr(((ParenthesizedExpression) expr).getExpression());
		} else if(expr instanceof CastExpression){
			return cast((CastExpression) expr);
		} else if(expr instanceof ConditionalExpression){
			return conditional((ConditionalExpression) expr);
		} else if(expr instanceof ArrayCreation){
			return arrayCreation((ArrayCreation) expr);
		} else if(expr instanceof ArrayAccess){
			return literal((ArrayAccess) expr);
		} else if(expr instanceof ArrayInitializer){
			return arrayInitializer((ArrayInitializer) expr);
		} else if(expr instanceof SuperMethodInvocation){
			return superInvocation( (SuperMethodInvocation) expr );
		} else if(expr instanceof InstanceofExpression){
			return instanceOfExpression( (InstanceofExpression) expr );
		}

		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new NotYetImplemented(start,stop);
	}

	private IASTRE superInvocation(SuperMethodInvocation expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		String name = expr.getName().getFullyQualifiedName();
		IASTRE exprCallee = new ASTLiteral(start, start+5, "super");
		List<IASTRE> pars = new ArrayList<>();
		for(Object p : expr.arguments()){
			pars.add(
					getExpr( (ASTNode) p )
			);
		}
		ASTMethodCall mc = new ASTMethodCall(start,stop, name, exprCallee, pars );
		IMethodBinding method = expr.resolveMethodBinding();
		String pkg, nmm;
		if(method != null){
			ITypeBinding theClass = method.getDeclaringClass();
			pkg = theClass.getPackage().getName();
			nmm = theClass.getName();
			String classPointed = pkg + "." + nmm;
			if(classPointed.startsWith(".")){
				classPointed = classPointed.substring(1);
			}
			mc.setClassPointed(classPointed);
			//System.out.println(node.toString() + " points to " + classPointed);
		} else {
			ITypeBinding objType = expr.resolveTypeBinding();
			if(objType != null){
				IMethodBinding[] methods = objType.getDeclaredMethods();
				for(IMethodBinding m : methods){
					if(m.getName().equals(expr.getName().getFullyQualifiedName()) && m.getParameterTypes().length == pars.size()) {
						ITypeBinding theClass = m.getDeclaringClass();
						pkg = theClass.getPackage().getName();
						nmm = theClass.getName();
						if(nmm.contains("<")){
							nmm = nmm.substring(0, nmm.indexOf("<"));
						}
						String classPointed = pkg + "." + nmm;
						if(classPointed.startsWith(".")){
							classPointed = classPointed.substring(1);
						}
						mc.setClassPointed(classPointed);
					}
				}
			}
		}
		return mc;
	}

	private IASTRE arrayInitializer(ArrayInitializer expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		List<IASTRE> elms = new ArrayList<>();
		for(Object o : expr.expressions()){
			if(o instanceof ASTNode){
				elms.add( getExpr((ASTNode)o));
			}
		}
		return new ASTArrayInitializer(start,stop, elms);
	}

	private IASTRE arrayCreation(ArrayCreation expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();

		String type = expr.getType().getElementType().toString();
		List<IASTRE> pars = new ArrayList<>();

		for(Object o : expr.dimensions()){
			if(o instanceof Expression){
				pars.add( getExpr((Expression)o) );
			}
		}

		return new ASTNewObject(start,stop, type, true, pars);
	}

	private IASTRE conditional(ConditionalExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();

		IASTRE e = getExpr(expr.getExpression());
		IASTRE ethen = getExpr(expr.getThenExpression());
		IASTRE eelse = getExpr(expr.getElseExpression());

		return new ASTConditional(start,stop, e, ethen, eelse);
	}

	private IASTRE cast(CastExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		String type = expr.getType().toString();
		IASTRE e = getExpr(expr.getExpression());
		return new ASTCast(start,stop, type, e);
	}

	private IASTRE variableDeclaration(VariableDeclarationExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		String type = expr.getType().toString();
		IASTRE ret = null;
		if(expr.fragments().size() > 1){
			//multiple definition
			List<IASTRE> vars = new ArrayList<>();
			for(Object o :expr.fragments()){
				VariableDeclarationFragment subVar = (VariableDeclarationFragment) o;
				int vStart, vStop;
				vStart = subVar.getStartPosition();
				vStop = vStart + subVar.getLength();
				IASTRE name = getExpr(subVar.getName());
				IASTRE e = getExpr(subVar.getInitializer());
				ASTVariableDeclaration v = new ASTVariableDeclaration(vStart, vStop, type, name, e);
				String pkg, nmm;
				ITypeBinding typePointed = subVar.resolveBinding() != null ? subVar.resolveBinding().getType() : null;
				if(typePointed != null){
					pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
					nmm = typePointed.getName();
					String classPointed = pkg + "." + nmm;
					if(classPointed.startsWith(".")){
						classPointed = classPointed.substring(1);
					}
					v.setTypePointed(classPointed);
					//System.out.println(node.toString() + " points to " + classPointed);
				}
				vars.add(v);
			}
			ret = new ASTVariableMultipleDeclaration(start,stop,type, vars);
		} else {
			for(Object o :expr.fragments()){
				VariableDeclarationFragment subVar = (VariableDeclarationFragment) o;
				int vStart, vStop;
				vStart = subVar.getStartPosition();
				vStop = vStart + subVar.getLength();
				IASTRE name = getExpr(subVar.getName());
				IASTRE e = getExpr(subVar.getInitializer());
				ASTVariableDeclaration v = new ASTVariableDeclaration(vStart, vStop, type, name, e);
				String pkg, nmm;
				ITypeBinding typePointed = subVar.resolveBinding() != null ? subVar.resolveBinding().getType() : null;
				if(typePointed != null){
					pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
					nmm = typePointed.getName();
					String classPointed = pkg + "." + nmm;
					if(classPointed.startsWith(".")){
						classPointed = classPointed.substring(1);
					}
					v.setTypePointed(classPointed);
					//System.out.println(node.toString() + " points to " + classPointed);
				}
				ret = v;
			}

		}
		return ret;
	}

	private IASTRE fieldAccess(FieldAccess expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE where;
		String who;
		where = getExpr(expr.getExpression());
		who = expr.getName().getIdentifier();
		return new ASTAttributeAccess(start,stop, where, who);
	}

	private IASTRE fieldAccess(QualifiedName expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE where;
		String who;
		where = getExpr(expr.getQualifier());
		who = expr.getName().getIdentifier();
		return new ASTAttributeAccess(start,stop, where, who);
	}

	private IASTRE instanceOfExpression(InstanceofExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE.OPERATOR op = IASTRE.OPERATOR.instanceOf;
		IASTRE l = getExpr(expr.getLeftOperand());
		Type t = expr.getRightOperand();
		IASTRE r = new ASTLiteral(t.getStartPosition(), t.getStartPosition()+t.getLength(), t.toString());
		return new ASTBinary(start,stop, l, r, op);
	}

	private IASTRE mathExpression(InfixExpression expr) {

		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE.OPERATOR op = getOperator(expr.getOperator().toString());
		IASTRE l = getExpr(expr.getLeftOperand());
		IASTRE r = getExpr(expr.getRightOperand());
		ASTBinary bin = new ASTBinary(start,stop, l, r, op);
		IASTRE prev = bin;
		for(Object o : expr.extendedOperands()){
			ASTNode extExpr = (ASTNode) o;
			IASTRE extended = getExpr(extExpr);
			int extstart, extstop;
			extstart = extExpr.getStartPosition();
			extstop = extstart + extExpr.getLength();
			ASTBinary ext = new ASTBinary(start, extstop, prev, extended, op);
			prev = ext;
		}
		return prev;
	}

	private IASTRE newObject(ClassInstanceCreation expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		String type = expr.getType().toString();
		List<IASTRE> pars = new ArrayList<>();
		for(Object p : expr.arguments()){
			pars.add(
					getExpr((ASTNode) p)
			);
		}
		ASTNewObject obj = new ASTNewObject(start,stop, type, false, pars);
		if(expr.getAnonymousClassDeclaration() != null){
			AnonymousClassDeclaration hc = expr.getAnonymousClassDeclaration();
			int st, sp;
			st = hc.getStartPosition();
			sp = st + hc.getLength();
			ASTHiddenClass c = new ASTHiddenClass(st, sp);
			obj.setHiddenClass(c);
			//attributes of the hidden class + methods
			for(Object node : hc.bodyDeclarations()){
				if(node instanceof FieldDeclaration){
					FieldDeclaration f = (FieldDeclaration) node;
					ASTClass.Visibility vis = ASTClass.Visibility.PRIVATE;
					/*if(f.modifiers().size() > 0){
						int i = 0;
						while(i < f.modifiers().size() && !(f.modifiers().get(i) instanceof Modifier)){
							i++;
						}
						try {
							Modifier m = (Modifier) f.modifiers().get(i);
							vis = Getter.visibility(m.toString());
						} catch (Exception e){
						}
					}*/
					String typeF = f.getType().toString();
					String name = "";
					ASTRE exprF = null;
					for(Object ovf : f.fragments()){
						VariableDeclarationFragment vf = (VariableDeclarationFragment) ovf;
						name = vf.getName().getFullyQualifiedName();
						exprF = getExprState(vf.getInitializer());
					}
					int ssF = 0; int stF = 0;
					ssF = f.getStartPosition();
					stF = ssF + f.getLength();
					ASTAttribute attribute = new ASTAttribute(ssF, stF, vis, typeF , name, exprF);
					String pkg, nmm;
					ITypeBinding typePointed = f.getType().resolveBinding();
					if(typePointed != null){
						pkg = typePointed.getPackage() != null ? typePointed.getPackage().getName() : "";
						nmm = typePointed.getName();
						String classPointed = pkg + "." + nmm;
						if(classPointed.startsWith(".")){
							classPointed = classPointed.substring(1);
						}
						attribute.setTypePointed(classPointed);
						//System.out.println(node.toString() + " points to " + classPointed);
					}
					c.addAttribute(attribute);
				}
				if(node instanceof MethodDeclaration){
					ASTClass bck = lastClass;
					lastClass = c;
					IASTHasStms bckm = lastMethod;
					((MethodDeclaration) node).accept(this);
					lastMethod = bckm;
					lastClass = bck;
				}
			}
			expr.setAnonymousClassDeclaration(null);
		}
		return obj;
	}

	private IASTRE literal(NullLiteral expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.toString());
	}


	private IASTRE literal(CharacterLiteral expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.toString());
	}

	private IASTRE literal(ArrayAccess expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.getArray().toString());
	}

	private IASTRE literal(TypeLiteral expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.toString());
	}

	private IASTRE literal(BooleanLiteral expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.toString());
	}

	private IASTRE literal(NumberLiteral expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.toString());
	}

	private IASTRE literal(ThisExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.toString());
	}

	private IASTRE literal(StringLiteral expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.getEscapedValue());
	}

	private IASTRE assignment(Assignment expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE.OPERATOR op = getOperator(expr.getOperator().toString());
		IASTRE l = getExpr(expr.getLeftHandSide());
		IASTRE r = getExpr(expr.getRightHandSide());
		return new ASTAssignment(start, stop, l, r, op );
	}

	private IASTRE postFix(PostfixExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE.ADDDEC op = IASTRE.ADDDEC.increment;
		if(expr.getOperator().toString().equals("--")){
			op = IASTRE.ADDDEC.decrement;
		}
		IASTRE e = getExpr(expr.getOperand());
		return new ASTPostOp(start, stop, e, op);
	}
	private IASTRE preFix(PrefixExpression expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		IASTRE e = getExpr(expr.getOperand());
		//unary expr not
		if(expr.getOperator().toString().equals("!")){
			return new ASTUnary(start,stop, IASTRE.OPERATOR.not, e);
		}
		//unary -num
		if(expr.getOperator().toString().equals("-")){
			return new ASTUnary(start,stop, IASTRE.OPERATOR.minus, e);
		}
		//normal pre inc/dec
		IASTRE.ADDDEC op = IASTRE.ADDDEC.increment;
		if(expr.getOperator().toString().equals("--")){
			op = IASTRE.ADDDEC.decrement;
		}
		return new ASTPreOp(start, stop, e, op);
	}

	private IASTRE simpleName(SimpleName expr) {
		int start, stop;
		start = expr.getStartPosition();
		stop = start + expr.getLength();
		return new ASTLiteral(start, stop, expr.getIdentifier());
	}

	private IASTRE methodInvocation(MethodInvocation node) {
		int start, stop;
		start = node.getStartPosition();
		stop = start + node.getLength();
		String name = node.getName().getFullyQualifiedName();
		IASTRE exprCallee = getExpr(node.getExpression());
		List<IASTRE> pars = new ArrayList<>();
		for(Object p : node.arguments()){
			pars.add(
					getExpr( (ASTNode) p )
			);
		}


		ASTMethodCall mc =  new ASTMethodCall(start,stop, name, exprCallee, pars );
		IMethodBinding method = node.resolveMethodBinding();
		String pkg, nmm;
		if(method != null){
			ITypeBinding theClass = method.getDeclaringClass();
			pkg = theClass.getPackage().getName();
			nmm = theClass.getName();
			String classPointed = pkg + "." + nmm;
			if(classPointed.startsWith(".")){
				classPointed = classPointed.substring(1);
			}
			mc.setClassPointed(classPointed);
			//System.out.println(node.toString() + " points to " + classPointed);
		} else {
			Expression e = node.getExpression();
			if(e != null){
				ITypeBinding objType = e.resolveTypeBinding();
				if(objType != null){
					IMethodBinding[] methods = objType.getDeclaredMethods();
					for(IMethodBinding m : methods){
						if(m.getName().equals(node.getName().getFullyQualifiedName()) && m.getParameterTypes().length == pars.size()) {
							ITypeBinding theClass = m.getDeclaringClass();
							pkg = theClass.getPackage().getName();
							nmm = theClass.getName();
							if(nmm.contains("<")){
								nmm = nmm.substring(0, nmm.indexOf("<"));
							}
							String classPointed = pkg + "." + nmm;
							if(classPointed.startsWith(".")){
								classPointed = classPointed.substring(1);
							}
							mc.setClassPointed(classPointed);
						}
					}
				}
			}


		}
		return mc;
	}

	public IASTRE.OPERATOR getOperator(String op){
		switch (op){
			case "=": return IASTRE.OPERATOR.equal;
			case "!=": return IASTRE.OPERATOR.notEqual;
			case "+": return IASTRE.OPERATOR.plus;
			case "-": return IASTRE.OPERATOR.minus;
			case "/": return IASTRE.OPERATOR.div;
			case "*": return IASTRE.OPERATOR.mul;
			case ">": return IASTRE.OPERATOR.greater;
			case ">=": return IASTRE.OPERATOR.greaterEqual;
			case "<": return IASTRE.OPERATOR.less;
			case "<=": return IASTRE.OPERATOR.lessEqual;
			case "==": return IASTRE.OPERATOR.equality;
			case "||": return IASTRE.OPERATOR.or;
			case "&&": return IASTRE.OPERATOR.and;
			case "%": return IASTRE.OPERATOR.mod;
			case "^": return IASTRE.OPERATOR.xor;
			case "<<": return IASTRE.OPERATOR.shiftLeft;
			case ">>": return IASTRE.OPERATOR.shiftRight;
			case ">>>": return IASTRE.OPERATOR.shiftRight;
			case "<<<": return IASTRE.OPERATOR.shiftLeft;
		}
		return IASTRE.OPERATOR.equal;
	}

}
