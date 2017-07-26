public interface Consensus {
    public void propose(int pid, int value);
    public int decide(int pid);
}
