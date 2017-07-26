public class Pointer {
    public Element ptr;
    public int count;
    public Pointer(Element initItem, int initCount) {
        ptr = initItem;
        count = initCount;
    }
    public synchronized void copyTo(Pointer x) {
        // assumes that x is accessed by at most one process
        x.ptr = ptr;
        x.count = count;
    }
    public synchronized boolean
            CAS(Pointer localPtr, Element newPtr, int newCount) {
        if ((ptr == localPtr.ptr) && (count == localPtr.count)) {
            ptr = newPtr;
            count = newCount;
            return true;
        } else
            return false;
    }
}
