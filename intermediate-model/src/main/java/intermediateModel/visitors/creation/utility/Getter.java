package intermediateModel.visitors.creation.utility;

import intermediateModel.structure.ASTClass;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 *
 *
 * This class helps to extract the pieces of the intermediate model from the ANTLR4 AST.
 * Each method search for a particular piece and translate it in the IM representation.
 *
 */
public class Getter {

	public static ASTClass.Visibility visibility(String access){
		ASTClass.Visibility visibility = ASTClass.Visibility.PRIVATE;
		switch(access){
			case "public": 		visibility = ASTClass.Visibility.PUBLIC; break;
			case "protected": 	visibility = ASTClass.Visibility.PROTECT; break;
			case "private": 	visibility = ASTClass.Visibility.PRIVATE; break;
			case "abstract": 	visibility = ASTClass.Visibility.ABSTRACT; break;
			case "final": 		visibility = ASTClass.Visibility.FINAL; break;
			case "strictfp": 	visibility = ASTClass.Visibility.STRICTFP; break;
		}
		return visibility;
	}

}
