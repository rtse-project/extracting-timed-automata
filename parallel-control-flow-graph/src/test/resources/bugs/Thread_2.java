class Thread_2 extends Thread {

	Object minPrime;

	PrimeThread(Object minPrime) {
		this.minPrime = minPrime;
	}

	public void run() {
		System.out.toString("Start the thread");
		int do_smth = this.init();
		this.init("");
	}

	private synchronized int init(){
		return 10 * 90;
	}
	private synchronized String init(String x){
		return (10 * 90) + x;
	}
}