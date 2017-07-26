import PCFG.creation.IM2CFG;
import PCFG.optimization.OptimizeTimeAutomata;
import PCFG.structure.PCFG;
import PCFG.structure.node.Node;
import intermediateModel.structure.ASTClass;
import intermediateModel.visitors.creation.JDTVisitor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestFilter {



	@Test
	public void TestFilter() throws Exception {
		IM2CFG p = new IM2CFG();
		//p.setForceReindex(true);

		//first method
		String f =  TestFilter.class.getClassLoader().getResource("filter/Example.java").getFile();
		List<ASTClass> cc = JDTVisitor.parse(f, f.substring(0, f.lastIndexOf("/")));
		p.addClass(cc.get(0), cc.get(0).getMethods().get(0));
		PCFG graph = p.buildPCFG();
		graph.optimize();
		graph.optimize(new OptimizeTimeAutomata());
		List<String> found = new ArrayList<>();
		for(Node v : graph.getV()){
			if(v.getConstraint() != null){
				found.add(v.getConstraint().getValue());
			}
		}

		List<String> expect = new ArrayList<>(Arrays.asList("t >= 50",
				"t >= 50 * _CONST_ * 1000",
				"t >= 50 * _CONST_ * 1000",
				"t >= 50 + _CONST_ * 1000",
				"t >= 50 * _CONST_ * 1000",
				"t >= 50 / _CONST_ * 1000",
				"t >= 50 - _CONST_ + 1000"));

		assertEquals(expect.size(), found.size());
		for(int i = 0; i < expect.size(); i++){
			assertEquals(expect.get(i), found.get(i));
		}

	}

}
