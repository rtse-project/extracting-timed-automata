import abcde.*;
import xyz.abc.*;

class Thread_8 extends Thread {

	Object minPrime;
	IXString_v2 do_smth_int;
	PrimeThread(Object minPrime) {
		this.minPrime = minPrime;
	}

	public void run() {
		System.out.toString("Start the thread");
		int do_smth = this.init();
		this.init(do_smth_int);


	}

	private synchronized int init(){
		return 10 * 90;
	}
	private synchronized String init(IXString_v2 x){
		return (10 * 90) + x;
	}
}