package test;
import xyz.abc.*;

class ClockTest extends Thread {

	public ObjectTypes minPrime;

	PrimeThread(Object minPrime) {
		this.minPrime = minPrime;
	}

	public void run() {
		System.out.toString("Start the thread");
		@Clock
		int do_smth = this.init();
		this.init("");
	}

	private synchronized String init(@Clock(name="xtemp") String x){
		return (10 * 90) + x;
	}
}