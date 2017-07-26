import java.util.Random;
class MProducer implements Runnable {
    BoundedBuffer2 b = null;
    public MProducer(BoundedBuffer2 initb) {
        b = initb;
        new Thread(this).start();
    }
    public void run() {
        double item;
        Random r = new Random();
        while (true) {
            item = r.nextDouble();
            System.out.println("produced item " + item);
            b.deposit(item);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
class MConsumer implements Runnable {
    BoundedBuffer2 b = null;
    public MConsumer(BoundedBuffer2 initb) {
        b = initb;
        new Thread(this).start();
    }
    public void run() {
        double item;
        while (true) {
            item = b.fetch();
            System.out.println("fetched item " + item);
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
        BoundedBuffer2 buffer = new BoundedBuffer2();
        MProducer producer1 = new MProducer(buffer);
        MProducer producer2 = new MProducer(buffer);
        MConsumer consumer1 = new MConsumer(buffer);
        MConsumer consumer2 = new MConsumer(buffer);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            return;
        }
    }
}

