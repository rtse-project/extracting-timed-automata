package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

/**
 * Class used to represent a XAL state.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

public class XALState extends XALItem {

    protected int _ID = -1;
    protected String name;
    protected String nameAction = null;
    protected String nameMetric = null;
    protected boolean isSynchronized = false;
    protected String style = "";
    protected String timeConstraint = "";

    /**
     * Enumeration that allows the constructor to chose between action or metric
     */
    public enum TypeProduction {
        ACTION,
        METRIC
    }

    /**
     * Construct a XALState object with only the ID
     * @param name The name of the state
     */
    public XALState(String name) {
        this.name = name;
    }

    /**
     * Construct a XALState object with an action or a metric. In order to decnamee which one use, the {@link TypeProduction} parameter is used.
     * @param name                name of the state
     * @param name_action_metric  name of the action/metric
     * @param production        ACTION | METRIC
     */
    public XALState(String name, String name_action_metric, TypeProduction production) {
        this.name = name;
        if(production == TypeProduction.ACTION)
            this.nameAction = name_action_metric;
        else
            this.nameMetric = name_action_metric;
    }

    /**
     * Construct a XALState object with an action and a metric.
     * @param name        name of the state
     * @param nameAction  name of the action
     * @param nameMetric  name of the metric
     */
    public XALState(String name, String nameAction, String nameMetric) {
        this.name = name;
        this.nameAction = nameAction;
        this.nameMetric = nameMetric;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getId() {
        return _ID;
    }

    public void setNumericID(int id){
        this._ID = id;
    }

    public int getNumericID(){
        return this._ID;
    }

    public String getIdAction() {
        return nameAction;
    }

    public String getIdMetric() {
        return nameMetric;
    }

    public String getTimeConstraint() {
        return timeConstraint;
    }

    public void setTimeConstraint(String timeConstraint) {
        this.timeConstraint = timeConstraint;
    }

	public String getNameAction() {
		return nameAction;
	}

	public void setNameAction(String nameAction) {
		this.nameAction = nameAction;
	}

	public String getNameMetric() {
		return nameMetric;
	}

	public void setNameMetric(String nameMetric) {
		this.nameMetric = nameMetric;
	}

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    @Override
    public String toString(int tab) {
        String out = "";
        out += tab(tab) + String.format("<State Id=\"%s\" Desc=\"%s\" ", this._ID, this.name);
        if(nameMetric != null)
            out += String.format("IdMetric=\"%s\" ", this.nameMetric);
        if(nameAction != null)
            out += String.format("IdAction=\"%s\" ", this.nameAction);
        if(style != null)
            out += String.format("style=\"%s\" ", this.style);
        if(isSynchronized){
			out += String.format("synchronized=\"%s\" ", this.isSynchronized);
		}
        out += "/>";
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
