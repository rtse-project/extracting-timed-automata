public class BinarySemaphore {
    boolean value;
    BinarySemaphore(boolean initValue) {
        value = initValue;
    }
    public synchronized void P() {
        while (value == false) 
            Util.myWait(this);// in queue of blocked processes
        value = false;
    }
    public synchronized void V() {
        value = true;
        notify();
    }
}
