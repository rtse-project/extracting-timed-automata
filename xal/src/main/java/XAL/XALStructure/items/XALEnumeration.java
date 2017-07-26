package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the list of constant inside an enumeration.
 *
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public class XALEnumeration extends XALItem {

    /**
     * Representation of a constant in XAL
     * @author Giovanni Liva (@thisthatDC)
     * @version %I%, %G%
     */
    public class XALConst extends XALItem {

        private String value;

        public XALConst(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString(int tab) {
            return null;
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


    private List<XALConst> enumeration;

    public XALEnumeration(List<XALConst> enumeration) {
        this.enumeration = enumeration;
    }

    public XALEnumeration() {
        enumeration = new ArrayList<XALConst>();
    }

    /**
     * Return the list of enumeration
     * @return List of {@link XALConst}
     */
    public List<XALConst> getInput() {
        return enumeration;
    }

    /**
     * Set the list of enumeration
     * @param enumeration  List of {@link XALConst}
     */
    public void setInput(List<XALConst> enumeration) {
        this.enumeration = enumeration;
    }

    /**
     * Add to the list of enumeration a constant
     * @param c Parameter to add to the List of {@link XALConst}
     */
    public void addInput(XALConst c){
        this.enumeration.add(c);
    }

    @Override
    public String toString(int tab) {
        return null;
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
