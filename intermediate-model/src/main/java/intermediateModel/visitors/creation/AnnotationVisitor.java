package intermediateModel.visitors.creation;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import timeannotation.definition.Annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class AnnotationVisitor {
	private static List<Annotation> getAnnotationVariable(List modifiers, String name){
		List<Annotation> annotations = new ArrayList<>();
		/*for(Object o : modifiers){
			if(o instanceof MarkerAnnotation ||
				o instanceof NormalAnnotation){
				Annotation ann = Parser.getAnnotation( o.toString() );
				if(ann instanceof ClockAnnotation && ((ClockAnnotation) ann).getName() == null){
					((ClockAnnotation) ann).setName(name);
				}
				annotations.add( ann );
			}
		}*/
		return annotations;
	}

	public static List<Annotation> getAnnotationVariable(List modifiers, SingleVariableDeclaration p) {
		return getAnnotationVariable(modifiers, p.getName().toString());
	}
	public static List<Annotation> getAnnotationVariable(List modifiers, VariableDeclarationFragment p) {
		return getAnnotationVariable(modifiers, p.getName().toString());
	}


}
