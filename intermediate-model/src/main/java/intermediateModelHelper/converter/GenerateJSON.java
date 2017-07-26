package intermediateModelHelper.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import intermediateModel.structure.ASTClass;

/**
 *
 * The class is responsible to convert the IM to the XAL representation.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class GenerateJSON implements IConverterIM {

	@Override
	public String convert(ASTClass startElement) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(startElement);
		} catch (JsonProcessingException e) {
			System.err.println("Impossible to convert");
			System.err.println(e.getMessage());
		}
		return "{}";
	}


}
