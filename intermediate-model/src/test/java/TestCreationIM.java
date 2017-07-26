import intermediateModel.interfaces.IASTMethod;
import intermediateModel.structure.ASTClass;
import intermediateModel.structure.ASTWhile;
import intermediateModel.visitors.DefaultASTVisitor;
import intermediateModel.visitors.creation.JDTVisitor;

import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class TestCreationIM {
	@Test
	public void TestDeclarationVariable() throws Exception {
		String f = TestCreationIM.class.getClassLoader().getResource("env/CreateListDeclaredVar.java").getFile();
		ASTClass c = JDTVisitor.parse(f, f.substring(0, f.lastIndexOf("/"))).get(0);
		IASTMethod m = c.getAllMethods().get(0);
		m.setDeclaredVars();
		assertEquals("Dequeue", m.getName());
		assertEquals(4, m.getDeclaredVar().size());
		//System.out.println(Arrays.toString(m.getDeclaredVar().toArray()));
	}

	@Test
	public void TestDeclarationAttribute() throws Exception {
		String f = TestCreationIM.class.getClassLoader().getResource("env/ConcQueue.java").getFile();
		ASTClass c = JDTVisitor.parse(f, f.substring(0, f.lastIndexOf("/"))).get(0);
		assertEquals(2, c.getAttributes().size());
	}

	@Test
	public void TestASTLabel() throws Exception {
		ClassLoader classLoader = TestCreationIM.class.getClassLoader();
		String file = classLoader.getResource("annotations/Label.java").getFile();
		List<ASTClass> cs = JDTVisitor.parse(file, file.substring(0, file.lastIndexOf("/")));
		assertEquals(1, cs.size());
		ASTClass c = cs.get(0);
		final int[] nLabel = {0};
		assertEquals(2, c.getMethods().size());
		c.getMethods().get(0).visit(new DefaultASTVisitor() {
			@Override
			public void enterASTWhile(ASTWhile elm) {
				if(elm.getIdentifier() != null){
					nLabel[0]++;
					assertEquals("mywhile", elm.getIdentifier());
				}
			}
		});
		assertEquals(1, nLabel[0]);
	}
	@Test
	public void TestASTLabel_singleChild() throws Exception {
		ClassLoader classLoader = TestCreationIM.class.getClassLoader();
		String file = classLoader.getResource("annotations/Label.java").getFile();
		List<ASTClass> cs = JDTVisitor.parse(file, file.substring(0, file.lastIndexOf("/")));
		assertEquals(1, cs.size());
		ASTClass c = cs.get(0);
		IASTMethod m = c.getFirstMethodByName("issueReopen");
		assertEquals(1, m.getStms().size());
		final int[] nLabel = {0};
		m.visit(new DefaultASTVisitor() {
			@Override
			public void enterASTWhile(ASTWhile elm) {
				if(elm.getIdentifier() != null){
					nLabel[0]++;
					assertEquals("outer", elm.getIdentifier());
				}
			}
		});
		assertEquals(1, nLabel[0]);

	}
}
