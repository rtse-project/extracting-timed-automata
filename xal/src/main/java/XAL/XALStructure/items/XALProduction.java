package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

/**
 * The reason behind this class is to allow to the class {@link XALActionPool} to handle both {@link XALAction} and {@link XALMetric}.
 * It uses the subclass {@link XALSystem}
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class XALProduction extends XALItem {
    protected String id;
    protected ProductionType type;

    /**
     * Enumeration used to discriminate between a call to an automaton or a function
     */
    public enum ProductionType {
        automaton,
        object
    }

    /**
     * This subclass of {@link XALProduction} is used to store the data for the System tag of a metric or action.
     * The value of className can be empty only if the metric/action has type Object.
     * This constrain is checked inside the classes {@link XALMetric} and {@link XALAction}
     * @author Giovanni Liva (@thisthatDC)
     * @version %I%, %G%
     */
    protected class XALSystem extends XALItem{
        private String className = null;
        private String name;
        private String path;

        public XALSystem(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public XALSystem(String className, String name, String path) {
            this.className = className;
            this.name = name;
            this.path = path;
        }

        public String getClassName() {
            return className;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString(int tab) {
            return tab(tab) + "<System Name=\"" + this.name + "\" Path=\"" + this.path + "\"/>\n";
        }

        @Override
        protected boolean checkConstriant() {
            return false;
        }
    }

    public XALProduction(String id, ProductionType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public ProductionType getType() {
        return type;
    }



}
