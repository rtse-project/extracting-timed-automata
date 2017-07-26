package XAL.XALStructure.items;

import XAL.XALStructure.XALItem;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Root element of a XAL document. It handles a whole XAL file.
 * The task of the class is just to collect the list of automaton that are inside a XAL file.
 *
 * @author      Giovanni Liva (@thisthatDC)
 * @version     %I%, %G%
 */

public class XALDocument extends XALItem {

    private List<XALAutomaton> automatons = new ArrayList<XALAutomaton>();
    private String filename = "";

    public XALDocument(String filename){
        this.filename = filename;
    }
    public XALDocument(String filename, List<XALAutomaton> l){
        this.automatons = l;
        this.filename = filename;
    }
    public XALDocument(String filename, XALAutomaton a){
        this.automatons.add(a);
        this.filename = filename;
    }

    public void addAutomaton(XALAutomaton a){
        automatons.add(a);
    }
    public List<XALAutomaton> getAutomatons() {
        return automatons;
    }

    public String getFilename() {
        return filename + (filename.endsWith(".xal") ? "" : ".xal");
    }

    /**
     * It generate the xal representation in a file, called <i>trace.xal</i>, on the current working path.
     */
    public void toFile() {
        toFile(this.filename);
    }

    /**
     * It generate the xal representation in the filepath passed as parameter.
     * @param filename  The path of where store the XAL file.
     */
    public void toFile(String filename){
        if(!filename.endsWith(".xal"))
            filename += ".xal";
        try {
            PrintWriter out = new PrintWriter(filename);
            out.println(this.toString());
            out.close();
        } catch (Exception e) {
            //If a system IO error occurs, it is not a problem. Just report that it happened.
            System.err.println("Error in generating XAL file : " + e.getMessage());
        }
    }

    @Override
    public String toString(){
        String out = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
        out += "<XAL>\n";
        for(XALAutomaton a : this.automatons){
            out += a.toString(1);
        }
        out += "</XAL>";
        return out;
    }

    public String toString(int tab) {
        return null;
    }

    @Override
    protected boolean checkConstriant() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XALDocument)) return false;
        XALDocument that = (XALDocument) o;
        return getFilename() != null ? getFilename().equals(that.getFilename()) : that.getFilename() == null;
    }

    public XALState getNodeFromNumericID(int id){
        for(XALAutomaton a : this.automatons){
            XALState s = a.getNodeFromNumericID(id);
            if(s != null){
                return s;
            }
        }
        return null;
    }

	public XALState getSyncNodeFromNumericID(int id) {
		for(XALAutomaton a : this.automatons){
			XALState s = a.getSyncNodeFromNumericID(id);
			if(s != null){
				return s;
			}
		}
		return null;
	}
}
