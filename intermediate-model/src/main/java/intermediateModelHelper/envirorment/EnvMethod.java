package intermediateModelHelper.envirorment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
class EnvMethod {
	private String className;
	private String methodName;
	private boolean istimeRelevant;
	private List<String> signature = new ArrayList<>();

	public EnvMethod(String className, String methodName, List<String> signature) {
		this.className = className;
		this.methodName = methodName;
		this.signature = signature;
	}

	public EnvMethod(String className, List<String> signature) {
		this.className = className;
		this.signature = signature;
	}

	public void setSignature(List<String> signature) {
		this.signature = signature;
	}

	public EnvMethod(String className, boolean istimeRelevant) {
		this.className = className;
		this.istimeRelevant = istimeRelevant;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getClassName() {
		return className;
	}

	public boolean istimeRelevant() {
		return istimeRelevant;
	}

	public void setIstimeRelevant(boolean istimeRelevant) {
		this.istimeRelevant = istimeRelevant;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EnvMethod envMethod = (EnvMethod) o;

		if (className != null ? !className.equals(envMethod.className) : envMethod.className != null) return false;
		if (methodName != null ? !methodName.equals(envMethod.methodName) : envMethod.methodName != null) return false;
		//return signature != null ? signature.equals(envMethod.signature) : envMethod.signature == null;
		return signature != null ? signature.size() == envMethod.signature.size() : envMethod.signature == null;
	}

	@Override
	public int hashCode() {
		int result = className != null ? className.hashCode() : 0;
		result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
		result = 31 * result + (signature != null ? signature.hashCode() : 0);
		return result;
	}
}
