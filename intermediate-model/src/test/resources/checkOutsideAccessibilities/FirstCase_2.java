class Thread_2 extends Thread {

	Object minPrime;


	PrimeThread(Object abc) {
		minPrime = abc;
	}

	public void run() {
		synchronized (minPrime) {
			String do_smth;
			var.init(do_smth);
			var.init();
		}
	}
}