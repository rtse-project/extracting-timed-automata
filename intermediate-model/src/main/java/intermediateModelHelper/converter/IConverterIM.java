package intermediateModelHelper.converter;

import intermediateModel.structure.ASTClass;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface IConverterIM {
	Object convert(ASTClass startElement);
}
