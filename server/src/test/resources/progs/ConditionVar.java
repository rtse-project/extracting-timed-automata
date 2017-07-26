public class ConditionVar {
    public synchronized void myWait() {
        System.out.println("waiting");
        try {
            wait();
        } catch (InterruptedException e) { }
        System.out.println("finished waiting");
    }
    public synchronized void myNotify() {
        notify();
    }
    public synchronized void myNotifyAll() {
        notifyAll();
    }
}
