public class SRSW {
    int value;
    int ts;
    public synchronized int getValue() {
	return value;
    }
    public synchronized int getTS() {
	return ts;
    }
    public synchronized void setValue(int x, int seq) {
        value = x;
        ts = seq;
    }
    public synchronized void setValue(SRSW x) {
        value = x.getValue();
        ts = x.getTS();
    }
}
