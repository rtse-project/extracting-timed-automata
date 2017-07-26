public class CompSwapConsensus implements Consensus {
    CompSwap x = new CompSwap(-1);
    int proposed[];
    public CompSwapConsensus(int n) {
        proposed = new int[n];
    }
    public void propose(int pid, int v) {
        proposed[pid] = v;
    }
    public int decide(int pid) {
        int j = x.compSwapOp(-1, pid);
        if (j == -1)
            return proposed[pid];
        else
            return proposed[j];
    }
}
