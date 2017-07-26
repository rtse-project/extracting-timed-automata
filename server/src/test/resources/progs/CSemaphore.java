public class CSemaphore {
    int value;
    BinarySemaphore mutex = new BinarySemaphore(true);
    BinarySemaphore s = new BinarySemaphore(false);
    public CSemaphore(int initValue) {
        value = initValue;
    }
    public void P() {
        mutex.P();
        value--;
        while (value < 0) {
            mutex.V();
            s.P();
            mutex.P();
        }
        mutex.V();
    }
    public void V() {
        mutex.P();
        value++;
        if (value <= 0) s.V();
        mutex.V();
    }
}
