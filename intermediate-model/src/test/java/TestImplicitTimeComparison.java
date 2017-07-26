import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.ApplyHeuristics;
import intermediateModel.visitors.creation.JDTVisitor;
import intermediateModelHelper.heuristic.definition.ImplicitTimeComparisonApplication;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestImplicitTimeComparison {

	ASTClass c;
	List<IASTMethod> m = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		String f =  TestImplicitTimeComparison.class.getClassLoader().getResource("implicitTimeComparison/Example.java").getFile();
		List<ASTClass> cc = JDTVisitor.parse(f, f.substring(0, f.lastIndexOf("/")));
		c = cc.get(0);
		m = c.getMethods();
	}

	@Test
	public void ClockTest() throws Exception {
		ApplyHeuristics ah = new ApplyHeuristics();
		ah.subscribe(ImplicitTimeComparisonApplication.class);
		ah.set__DEBUG__(false);
		ah.analyze(c);
		assertEquals(1, ah.getTimeConstraint().size());
	}
}
