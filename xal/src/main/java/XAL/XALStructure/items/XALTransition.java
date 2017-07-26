package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent a metric.
 * @TODO support the possibilities to use time constraint as well
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALTransition extends XALItem {

    private class ClockConstraint {
        private String ClockExp = "";

        private ClockConstraint(String clockExp) {
            ClockExp = escape(clockExp);
        }

        private String escape(String clockExp) {
            if(clockExp.startsWith(".")){
                clockExp = clockExp.substring(1);
            }
            clockExp = clockExp.replace("<", "&lt;");
            clockExp = clockExp.replace(">", "&gt;");
            return clockExp;
        }

        @Override
        public String toString() {
            return String.format("<ClockConstraint ClockExp=\"%s\"/>\n", this.ClockExp);
        }
    }

    public static final String METRIC_TRUE = "true";
    public static final String METRIC_FALSE = "false";

    private int from;
    private int to;
	private XALState fromState;
	private XALState toState;
    private String metricValue = null;
    private String style;
    private ClockConstraint clock = null;
    private List<String> clockReset = new ArrayList<>();

    public XALTransition(XALState from, XALState to) {
        this.from = from.getId();
        this.to = to.getId();
		this.fromState = from;
		this.toState = to;
        this.style = "[]";
    }


    public XALTransition(XALState from, XALState to, String metricValue) {
        this(from,to);
        this.metricValue = metricValue;
    }

    public XALTransition(XALState from, XALState to, String metricValue, String clockExpr) {
        this(from,to, metricValue);
        this.clock = new ClockConstraint(clockExpr);
    }


	public String getMetricValue() {
        return metricValue;
    }

    public int getTo() {
        return to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    @Override
    public String toString(int tab) {
        String out = tab(tab) + String.format("<Transition IdInputState=\"%s\" IdOutputState=\"%s\" ",this.from, this.to);
        if(this.metricValue != null && !this.metricValue.equals(""))
            out += String.format("MetricValue=\"%s\" ",this.metricValue);
        out += String.format("style=\"%s\"", this.style );
		if(clock != null || clockReset.size() > 0){
			out += ">\n";
			for(String reset : clockReset){
                out += tab(tab+1) + reset;
            }
            if(clock != null)
			    out += tab(tab+1) + clock.toString();
			out += tab(tab) + "</Transition>";
		} else {
			out += " />";
		}
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
        return false;
    }

    public void addClockReset(String clockReset) {
        this.clockReset.add(String.format("<ClockReset ClockVar=\"%s\"/>\n", clockReset));
    }
}
