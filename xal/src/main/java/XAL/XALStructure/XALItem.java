package XAL.XALStructure;

/**
 * @author Giovanni Liva (@thisthatDC)
 * @version %I%, %G%
 */
public abstract class XALItem {

    public abstract String toString(int tab);

    protected abstract boolean checkConstriant();

    protected String tab(int n){
        if(n == 0)
            return "";
        else
            return "\t" + tab(n-1);
    }


}
