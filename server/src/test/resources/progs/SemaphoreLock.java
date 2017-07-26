class SemaphoreLock implements Lock {
    BinarySemaphore mutex = new BinarySemaphore(true);
    public void requestCS(int i) {
        mutex.P();
    }
    public void releaseCS(int i) {
        mutex.V();
    }
}
