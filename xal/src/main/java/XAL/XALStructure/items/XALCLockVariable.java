package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

/**
 * Class used to represent a time variable
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALCLockVariable extends XALItem {
    protected String name;

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public XALCLockVariable(String name) {
        this.name = name;
    }

    @Override
    public String toString(int tab) {
        return tab(tab) + String.format("<Variable Name=\"%s\" />", this.name);
    }

    @Override
    public String toString(){
        return this.toString(0);
    }

    /**
     * There are no constraint to validate
     * @return  Always true
     */
    protected boolean checkConstriant() {
        return true;
    }
}
