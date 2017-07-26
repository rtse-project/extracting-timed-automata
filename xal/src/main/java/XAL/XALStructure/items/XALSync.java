package XAL.XALStructure.items;

import XAL.XALStructure.XALAddState;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent a set of {@link XALState} that are in a synchronized block.
 * It implements the interface {@link XALAddState}.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

public class XALSync extends XALState implements XALAddState {


	protected int _ID = -1;
    List<XALState> states = new ArrayList<XALState>();
    XALAddState parent;

    /**
     * * Check the super method in {@link XALState}
     * @param id The id of the state
     * @param parent Parent
     */
    public XALSync(String id, XALAddState parent) {
        super(id);
        this.parent = parent;
    }

    /**
     * Check the super method {@link XALState}
     * @param id                name of the state
     * @param id_action_metric  name of the action/metric
     * @param production        ACTION | METRIC
     */
    public XALSync(String id, String id_action_metric, TypeProduction production) {
        super(id,id_action_metric, production);
    }

    /**
     * Check the super method {@link XALState}
     * @param id        name of the state
     * @param idAction  name of the action
     * @param idMetric  name of the metric
     */
    public XALSync(String id, String idAction, String idMetric) {
        super(id,idAction,idMetric);
    }


	public int getNumericID() {
		return _ID;
	}

	public void setNumericID(int _ID) {
		this._ID = _ID;
	}

	/**
     * Add a state to the list of states inside a synchronized block
     * If the name already exists in the list, it will use a numeric index to make it unique.
     * @param state The {@link XALState} to add
     */
    @Override
    public void addState(XALState state){
        /*int i  = 0;
        String id = state.getId();
        while(existState(state.getId())) {
            state.setId(id + "_" + i++);
        }*/
        this.states.add(state);
    }

    public boolean existState(XALState s){
        return parent.existState(s.getId());
    }

	public boolean existState(int s){
        return parent.existState(s);
    }

    /**
     * Get the list of states inside the synchronized block
     * @return  A list of {@link XALState}
     */
    public List<XALState> getStates() {
        return states;
    }

    @Override
    public String toString(int tab) {
        String out = "";
        out += tab(tab) + String.format("<Sync Id=\"%s\" ", super.getId());
        if(style != null)
            out += String.format("style=\"%s\" ", this.style);
        out += ">\n";
        for (XALState v: this.states ) {
            out += v.toString(tab+1) + "\n";
        }
        out += tab(tab) + "</Sync>";
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

    public XALState getNodeFromNumericID(int i) {
        for(XALState s : this.getStates()){
            if(s instanceof XALSync) {
                XALState r = ((XALSync)s).getNodeFromNumericID(i);
                if(r != null) {
                    return r;
                }
            }
            else if(s.getNumericID() == i){
                return s;
            }
        }
        return null;
    }

	public XALSync getSyncNodeFromNumericID(int id) {
		for(XALState s : this.getStates()){
			if(s instanceof XALSync) {
				if(s.getNumericID() == id){
					return (XALSync) s;
				} else {
					return ((XALSync)s).getSyncNodeFromNumericID(id);
				}
			}
		}
		return null;
	}
}
