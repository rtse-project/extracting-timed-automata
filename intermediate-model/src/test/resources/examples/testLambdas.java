import XAL.XALStructure.XALAddState;
import XAL.XALStructure.XALItem;
import XAL.XALStructure.exception.XALMalformedException;
import XAL.XALStructure.items.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Xal Automaton. This class represent the structure of a single automaton.
 * It implements the interface {@link XALAddState}.
 * It stores information about
 * <ul>
 * <li>{@link XALGlobalState} :: List of variables</li>
 * <li>{@link XALClock} :: List of clock variables</li>
 * <li>{@link XALActionPool} :: List of action and metrics @see "{@link XALAction} and {@link XALMetric}"</li>
 * <li>{@link XALState} :: List of states</li>
 * <li>Initial State</li>
 * <li>List of end States</li>
 * <li>{@link XALTransition} :: List of transition between {@link XALState}</li>
 * </ul>
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */

public class testLambdas extends XALItem implements XALAddState {
	private static int __counter = 0;
	public static final String __HIDDEN_AUTOMATA__ = "NoName_";

	private XALGlobalState globalState;
	private String id;
	private XALClock clocks;
	private XALActionPool actionPool;
	private List<XALState> states;
	private String initialState;
	private List<String> finalStates;
	private List<XALTransition> transitions;



	public testLambdas() {
		this.states      = new ArrayList<XALState>();
		this.transitions = new ArrayList<XALTransition>();
		this.finalStates = new ArrayList<String>();
		this.globalState = new XALGlobalState();
		this.clocks      = new XALClock();
		this.actionPool  = new XALActionPool();
		this.id = __HIDDEN_AUTOMATA__ + __counter++;
	}

	public testLambdas(String id) {
		this.states      = new ArrayList<XALState>();
		this.transitions = new ArrayList<XALTransition>();
		this.finalStates = new ArrayList<String>();
		this.globalState = new XALGlobalState();
		this.clocks      = new XALClock();
		this.actionPool  = new XALActionPool();
		this.id = id;
	}

	/**
	 * Get the {@link XALGlobalState} of the automata
	 * @return the list of variables of the automata
	 */
	public XALGlobalState getGlobalState() {
		return globalState;
	}
	/**
	 * Set the {@link XALGlobalState} of the automata
	 * @param globalState list of variables for the automata
	 */
	public void setGlobalState(XALGlobalState globalState) {
		this.globalState = globalState;
	}


	/**
	 * If the automaton is created on method definition, it will return its name
	 * @return the id of the automaton
	 */
	public String getId(){
		return this.id;
	}

	/**
	 * return the list of states inside the automata
	 * @return The list of states in the automata
	 */
	public List<XALState> getStates() {
		return states;
	}

	/**
	 * Set the list of states of the automata
	 * @param states    List of {@link XALState} to uses as automaton states
	 */
	public void setStates(List<XALState> states) {
		this.states = states;
	}

	/**
	 * Add a {@link XALState} to the list of states of the automaton.
	 * If the name already exists in the list, it will use a numeric index to make it unique.
	 * @param s {@link XALState} to add
	 */
	@Override
	public void addState(XALState s) {
		int i  = 0;
		String id = s.getId();
		while(existState(s.getId())) {
			s.setId(id + "_" + i++);
		}
		this.states.add(s);
	}

	private boolean existState(XALState s){
		return this.existState(s.getId());
	}

	private boolean existState(String s){
		return this.states.stream().anyMatch(state -> (state.getId().equals(s)));
	}


	/**
	 * Get the id of the init state of the automaton
	 * @return the String that correspond to the ID of the {@link XALState}
	 */
	public String getInitialState() {
		return initialState;
	}

	/**
	 * Set the ID of the initial state of the automaton
	 * @param state {@link XALState} to use as initial state
	 * @throws XALMalformedException when the id passed as parameter does not exist in the list of state of the automaton
	 */
	public void setInitialState(XALState state) throws XALMalformedException {
		setInitialState(state.getId());
	}

	/**
	 * Set the ID of the initial state of the automaton
	 * @param initialState  String that represent the ID of the {@link XALState} to use as initial state
	 * @throws  XALMalformedException when the id passed as parameter does not exist in the list of state of the automaton
	 */
	public void setInitialState(final String initialState) throws XALMalformedException {
		if(!this.states.stream().anyMatch( s -> (s.getId().equals(initialState)))){
			throw new XALMalformedException("Initial state ID not found in the list of states");
		}
		this.initialState = initialState;
	}


	/**
	 * return the list of final states ids inside the automaton.
	 * @return The list of ids corresponding to the final {@link XALState} of the automaton.
	 */
	public List<String> getFinalStates() {
		return finalStates;
	}

	/**
	 * Set the list of final states of the automaton
	 * @param states    List of ids of the corresponding {@link XALState} to use as final state
	 */
	public void setFinalStates(List<String> states) {
		this.finalStates = states;
	}

	/**
	 * Add an id to the list of final states of the automaton
	 * @param s id to add
	 */
	public void addFinalState(String s) {
		this.finalStates.add(s);
	}


	/**
	 * Add the id of the state to the list of final states of the automaton
	 * @param s {@link XALState} to add
	 */
	public void addFinalState(XALState s) {
		this.finalStates.add(s.getId());
	}


	/**
	 * Get all the transition of the automaton
	 * @return List of {@link XALTransition}
	 */
	public List<XALTransition> getTransitions() {
		return transitions;
	}

	/**
	 * Set the list of automaton transition
	 * @param transitions   list of {@link XALTransition} to use
	 */
	public void setTransitions(List<XALTransition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * Add a single {@link XALTransition} to the list of transiiton
	 * @param t     {@link XALTransition} to add
	 */
	public void addTransition(XALTransition t) {
		this.transitions.add(t);
	}

	@Override
	public String toString() {
		return toString(0);
	}

	@Override
	public String toString(int tab) {
		String out = "";
		out += tab(tab) + String.format("<Automaton Id=\"%s\">\n", this.id);
		out += this.globalState.toString(tab+1);
		out += this.clocks.toString(tab+1);
		out += this.actionPool.toString(tab+1);
		out += printStates(tab+1);
		out += printInitState(tab+1);
		out += printFinalStates(tab+1);
		out += printTransition(tab+1);
		out += tab(tab) + "</Automaton>\n";
		return out;
	}

	private String printTransition(int tab) {
		String out = "";
		if(this.transitions.size() == 0){
			return tab(tab) + "<Transitions/>\n";
		}
		out += tab(tab) + "<Transitions>\n";
		for (XALTransition v: this.transitions ) {
			out += v.toString(tab+1) + "\n";
		}
		out += tab(tab) + "</Transitions>\n";
		return out;
	}

	private String printInitState(int tab) {
		return tab(tab) + String.format("<InitialState IdState=\"%s\"/>\n", this.initialState);
	}

	private String printFinalStates(int tab) {
		String out = "";
		if(this.finalStates.size() == 0){
			return tab(tab) + "<FinalStates/>\n";
		}
		out += tab(tab) + "<FinalStates>\n";
		for (String v: this.finalStates ) {
			out += tab(tab+1) + String.format("<FinalState IdState=\"%s\"/>\n", v);
		}
		out += tab(tab) + "</FinalStates>\n";
		return out;
	}

	private String printStates(int tab) {
		String out = "";
		if(this.states.size() == 0){
			return tab(tab) + "<States/>\n";
		}
		out += tab(tab) + "<States>\n";
		for (XALState v: this.states ) {
			out += v.toString(tab+1) + "\n";
		}
		out += tab(tab) + "</States>\n";
		return out;
	}

	/**
	 * Check if the transition names are inside the metric enumeration
	 * @return Always true
	 */
	@Override
	protected boolean checkConstriant() {
		return true;
	}
}
