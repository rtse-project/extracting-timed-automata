package PCFG.structure;

import java.util.List;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public interface IHasCFG {
	void addCFG(CFG cfg);
	List<CFG> getCFG();
}
