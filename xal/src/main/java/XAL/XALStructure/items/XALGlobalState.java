package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

import java.util.ArrayList;
import java.util.List;

/**
 * It contains all the {@link XALVariable} of an automaton.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALGlobalState extends XALItem {

    private List<XALVariable> variables;


    public XALGlobalState(){
       this.variables = new ArrayList<XALVariable>();
    }

    public XALGlobalState(List<XALVariable> variables) {
        this.variables = variables;
    }

    public List<XALVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<XALVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(XALVariable v){
        if(!variables.contains(v))
            variables.add(v);
    }

    @Override
    public String toString(int tab) {
        String out = "";
        if(this.variables.size() == 0){
            return tab(tab) + "<GlobalState/>\n";
        }
        out += tab(tab) + "<GlobalState>\n";
        for (XALVariable v: this.variables ) {
            out += v.toString(tab+1) + "\n";
        }
        out += tab(tab) + "</GlobalState>\n";
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

    public boolean contains(XALVariable var) {
        return variables.contains(var);
    }
}
