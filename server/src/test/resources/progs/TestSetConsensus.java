class TestSetConsensus implements Consensus {
    TestAndSet x;
    int proposed[] = {0, 0};
    // assumes pid is 0 or 1
    public void propose(int pid, int v) {
        proposed[pid] = v;
    }
    public int decide(int pid) {
        if (x.testAndSet(pid) == -1)
            return proposed[pid];
        else
            return proposed[1 - pid];
    }
}

