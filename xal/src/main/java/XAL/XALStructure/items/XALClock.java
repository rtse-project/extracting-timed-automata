package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This structure stores all the clock variable of an automaton.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALClock extends XALItem {

    private List<XALCLockVariable> variables;

    public XALClock(List<XALCLockVariable> variables) {
        this.variables = variables;
    }

    public XALClock(){
        variables = new ArrayList<XALCLockVariable>();
    }

    public List<XALCLockVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<XALCLockVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(XALCLockVariable v){
        this.variables.add(v);
    }

    @Override
    public String toString(int tab) {
        String out = "";
        if(this.variables.size() == 0){
            return tab(tab) + "<Clocks/>\n";
        }
        out += tab(tab) + "<Clocks>\n";
        for (XALCLockVariable v: this.variables ) {
            out += v.toString(tab+1) + "\n";
        }
        out += tab(tab) + "</Clocks>\n";
        return out;
    }

    @Override
    public String toString(){
        return this.toString(0);
    }
    /**
     * No constraint to check
     * @return Always true
     */
    @Override
    protected boolean checkConstriant() {
        return true;
    }
}
