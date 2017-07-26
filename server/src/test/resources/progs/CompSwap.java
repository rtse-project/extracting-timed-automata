public class CompSwap {
    int myValue = 0;
    public CompSwap(int initValue) {
        myValue = initValue;
    }
    public synchronized int compSwapOp(int prevValue, int newValue) {
        int oldValue = myValue;
        if (myValue == prevValue)
            myValue = newValue;
        return oldValue;
    }
}

