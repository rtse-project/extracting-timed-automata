package XAL.XALStructure.items;

import java.util.List;


/**
 * This class represent a metric.
 * There are some consistency check to perform. A metric cannot be object and does not have a system.class value.
 * It uses some list to store the input and output {@link XALParameter} and it uses {@link XALEnumeration}.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALMetric extends XALProduction  {

    private XALSystem system;
    private List<XALParameter> input;
    private List<XALParameter> output;
    private XALEnumeration enumeration;

    public XALMetric(String id, ProductionType type, String className, String name, String path, XALEnumeration enumeration) {
        super(id, type);
        system = new XALSystem(className,name,path);
        this.enumeration = enumeration;
    }

    public XALMetric(String id, ProductionType type,  String name, String path, XALEnumeration enumeration) {
        super(id, type);
        system = new XALSystem(name,path);
        this.enumeration = enumeration;
    }

    /**
     * Return the list of input parameter to the metric
     * @return input List of {@link XALParameter}
     */
    public List<XALParameter> getInput() {
        return input;
    }

    /**
     * Set the list of input parameter to the metric
     * @param input    input List of {@link XALParameter}
     */
    public void setInput(List<XALParameter> input) {
        this.input = input;
    }

    /**
     * Add to the list of input parameter of the metric a new parameter
     * @param p Parameter to add to the input List of {@link XALParameter}
     */
    public void addInput(XALParameter p){
        this.input.add(p);
    }

    /**
     * Return the list of output parameter to the metric
     * @return output List of {@link XALParameter}
     */
    public List<XALParameter> getOutput() {
        return output;
    }

    /**
     * Set the list of output parameter to the metric
     * @param output     output List of {@link XALParameter}
     */
    public void setOutput(List<XALParameter> output) {
        this.output = output;
    }


    /**
     * Add to the list of output parameter of the metric a new parameter
     * @param p Parameter to add to the output List of {@link XALParameter}
     */
    public void addOutput(XALParameter p){
        this.output.add(p);
    }

    public XALSystem getSystem() {
        return system;
    }

    public XALEnumeration getEnumeration() {
        return enumeration;
    }

    @Override
    public String toString(int tab) {
        String out = "";
        out += tab(tab) + "<Metric Id=\"" + this.getId() + "\" Type=\"" + (this.type == ProductionType.automaton ? "automaton" : "object") + "\">\n";
        out += this.getSystem().toString(tab+1);
        out += tab(tab) + "</Metric>";
        return out;
    }

    /**
     * Checks if the type of the metric is coherent with the system
     * @return if the constraint is fulfilled
     */
    @Override
    protected boolean checkConstriant() {
        if(this.type == ProductionType.automaton){
            return system.getClass() != null;
        }
        return true;
    }
}
