class Thread_1 extends Thread {

	Object minPrime;


	PrimeThread(Object minPrime) {
		this.minPrime = minPrime;
	}

	public void run() {
		synchronized (minPrime) {
			String do_smth;
			var.init(do_smth);
			var.init();
		}
	}
}