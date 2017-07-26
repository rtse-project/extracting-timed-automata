class BoundedBuffer2 {
    final int sizeBuf = 10;
    double[] buffer = new double[sizeBuf];
    int inBuf = 0, outBuf = 0, count = 0;
    BinarySemaphore mutex = new BinarySemaphore(true);
    BinarySemaphore isEmpty = new BinarySemaphore(false);
    BinarySemaphore isFull = new BinarySemaphore(true);
    public void deposit(double value) {
        mutex.P();
        while (count == sizeBuf) {
            mutex.V();
            isFull.P();
            mutex.P();
        }
        buffer[inBuf] = value;
        inBuf = (inBuf + 1) % sizeBuf;
        count++;
        if (count == 1) isEmpty.V();
        mutex.V();
    }
    public double fetch() {
        double value;
        mutex.P();
        while (count == 0) {
            mutex.V();
            isEmpty.P();
            mutex.P();
        }
        value = buffer[outBuf];
        outBuf = (outBuf + 1) % sizeBuf;
        count--;
        if (count == sizeBuf - 1) isFull.V();
        mutex.V();
        return value;
    }
}

