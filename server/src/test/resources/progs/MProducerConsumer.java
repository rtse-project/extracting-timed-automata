import java.util.Random;
class MProducer extends Thread {
    BoundedBuffer3 b = null;
    public MProducer(BoundedBuffer3 initb) {
        b = initb;
        start();
    }
    public void run() {
        double item;
        Random r = new Random();
        while (true) {
            item = r.nextDouble();
            System.out.println(this.toString() + ": produced item " + item);
            b.deposit(item);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
class MConsumer extends Thread {
    BoundedBuffer3 b = null;
    public MConsumer(BoundedBuffer3 initb) {
        b = initb;
        start();
    }
    public void run() {
        double item;
        while (true) {
            item = b.fetch();
            System.out.println(this.toString() + ": consumed item " + item);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
class MProducerConsumer {
    public static void main(String[] args) {
        BoundedBuffer3 buffer = new BoundedBuffer3();
        MProducer producer1 = new MProducer(buffer);
        MProducer producer2 = new MProducer(buffer);
        MConsumer consumer1 = new MConsumer(buffer);
        MConsumer consumer2 = new MConsumer(buffer);
        // try { Thread.sleep(1000); } catch (InterruptedException e) { return; }
        try {
         producer1.join();
         producer2.join();
         consumer1.join();
         consumer2.join();
        } catch (InterruptedException e) {};
    }
}

