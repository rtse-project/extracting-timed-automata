public class CondVar {
    public synchronized void condWait() {
        try {
            wait();
        } catch (InterruptedException e) {
        }
    }
    public synchronized void condNotify() {
        notify();
    }
}
