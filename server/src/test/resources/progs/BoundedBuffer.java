class BoundedBuffer {
    final int size = 10;
    double[] buffer = new double[size];
    int inBuf = 0, outBuf = 0;
    BinarySemaphore mutex = new BinarySemaphore(true);
    CountingSemaphore isEmpty = new CountingSemaphore(0);
    CountingSemaphore isFull = new CountingSemaphore(size);

    public void deposit(double value) {
        isFull.P();// wait if buffer is full
        mutex.P(); // ensures mutual exclusion
        buffer[inBuf] = value; // update the buffer
        inBuf = (inBuf + 1) % size;
        mutex.V();
        isEmpty.V();  // notify any waiting consumer
    }
    public double fetch() {
        double value;
        isEmpty.P(); // wait if buffer is empty
        mutex.P();  // ensures mutual exclusion
        value = buffer[outBuf]; //read from buffer
        outBuf = (outBuf + 1) % size;
        mutex.V();
        isFull.V(); // notify any waiting producer
        return value;
    }
}

