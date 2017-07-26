package uppaal;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;


public class Declaration extends UppaalElement{
	List<String> declarations = new LinkedList<String>();;
	public Declaration(Element declarationsElement){
		String[] decls = declarationsElement.getText().split("\n");
		for(String decl : decls)
			declarations.add(decl);
	}
	
	public Declaration(Declaration declarations) {
		this.declarations.add(declarations.toString().trim());
	}
	
	public void add(Declaration declarations){
		this.declarations.add(declarations.toString().trim());
	}

	public Declaration(String declarations){
		this.declarations = new LinkedList<String>();
		this.declarations.add(declarations.toString().trim());
	}

	public Declaration() {
	}

	@Override
	public String getXMLElementName() {
		return "declaration";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String declaration : declarations){
			sb.append(declaration+"\n");
		}
		return sb.toString();
	}
	
	@Override
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.addContent(toString());
		return result;
	}

	public List<String> getStrings() {
		return declarations;
	}

	public void add(String s) {
		s = filter(s);
		if(s.equals("") || declarations.contains(s))
			return;
		declarations.add(s);
	}

	public static String filter(String v) {
		String out = v;
		if(v.matches("int [0-9].*")){
			return "";
		}
		return out;
	}

	public void remove(String s) {
		declarations.remove(s);
	}
	
}
