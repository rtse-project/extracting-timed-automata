package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

/**
 * The class represent the passage of parameters between metric/action @see "{@link XALMetric} and {@link XALAction}"
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALParameter extends XALItem {

    private String localVar;
    private String targetVar;

    public XALParameter(String localVar, String targetVar) {
        this.localVar = localVar;
        this.targetVar = targetVar;
    }

    public XALParameter(XALVariable localVar, String targetVar) {
        this.localVar = localVar.getName();
        this.targetVar = targetVar;
    }

    public String getLocalVar() {
        return localVar;
    }

    public String getTargetVar() {
        return targetVar;
    }

    @Override
    public String toString(int tab) {
        return "";
    }

    /**
     * No check
     * @return Always true
     */
    @Override
    protected boolean checkConstriant() {
        return true;
    }
}
