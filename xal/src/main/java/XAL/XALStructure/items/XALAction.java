package XAL.XALStructure.items;

import java.util.List;
import XAL.*;

/**
 * This class represent an action.
 * There are some consistency check to perform. An action can be only object (@see "{@link XAL.XALStructure.items.XALProduction.ProductionType}") as type.
 * It uses some list to store the input and output {@link XALParameter}.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALAction extends XALProduction  {

    private XALSystem system;
    private List<XALParameter> input;
    private List<XALParameter> output;

    public XALAction(String id, ProductionType type, String className, String name, String path) {
        super(id, type);
        system = new XALSystem(className,name,path);
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

    @Override
    public String toString(int tab) {
        return "";
    }

    /**
     * Control that action is an object.
     * @return true if the constraint is fulfilled
     */
    @Override
    protected boolean checkConstriant() {
        return this.type == ProductionType.object;
    }
}
