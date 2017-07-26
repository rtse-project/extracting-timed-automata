package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

import java.util.ArrayList;
import java.util.List;

/**
 * The following class handles metrics and actions of an {@link XALAutomaton}.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALActionPool extends XALItem {
    private List<XALProduction> action_metric;

    public XALActionPool(List<XALProduction> action_metric) {
        this.action_metric = action_metric;
    }

    public XALActionPool() {
        this.action_metric = new ArrayList<XALProduction>();
    }

    public List<XALProduction> getAction_metric() {
        return action_metric;
    }

    public void setAction_metric(List<XALProduction> action_metric) {
        this.action_metric = action_metric;
    }

    public void addProduction(XALProduction p){
        this.action_metric.add(p);
    }

    @Override
    public String toString(int tab) {
        String out = "";
        if(this.action_metric.size() == 0){
            return tab(tab) + "<ActionPool/>\n";
        }
        out += tab(tab) + "<ActionPool>\n";
        for (XALProduction v: this.action_metric ) {
            out += v.toString(tab+1) + "\n";
        }
        out += tab(tab) + "</ActionPool>\n";
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
