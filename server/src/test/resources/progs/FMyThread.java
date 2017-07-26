import java.util.Random;
public class FMyThread extends Thread {
    int myId;
    Lock lock;
    Random r = new Random();
    public FMyThread(int id, Lock lock) {
        myId = id;
        this.lock = lock;
    }
    void nonCriticalSection() {
        System.out.println(myId + " is not in CS");
        Util.mySleep(r.nextInt(1000));
    }
    void CriticalSection() {
        System.out.println(myId + " is in CS *****");
        // critical section code
        Util.mySleep(r.nextInt(1000));
    }
    public void run() {
        while (true) {
            lock.requestCS(myId);
            CriticalSection();
            lock.releaseCS(myId);
            nonCriticalSection();
        }
    }
    public static void main(String[] args) throws Exception {
        FMyThread t[];
        int N = 2;
        t = new FMyThread[N];
        Lock lock = new PetersonAlgorithm();//or any other mutex algorithm
        for (int i = 0; i < N; i++) {
            t[i] = new FMyThread(i, lock);
            t[i].start();
        }
    }
}
