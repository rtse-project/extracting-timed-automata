package test;

import intermediateModel.interfaces.IASTMethod;
import test.Thread_2;

class Thread_1 extends Thread {

	XYZ minPrime;
	IASTMethod m;
	Thread_2 var;
	String xyz;

	PrimeThread(Object minPrime) {
		this.minPrime = minPrime;
	}

	public void run0() {
		String do_smth;
		var.init(do_smth);
		var.init();
		synchronized (this){

		}
	}

	public void run1() {
		String do_smth;
		var.init(do_smth);
		var.init();
		synchronized (Thread_1.class){

		}
	}

	public void run2() {
		String do_smth;
		var.init(do_smth);
		var.init();
		synchronized (var){

		}
	}

	public void run3() {
		String do_smth;
		var.init(do_smth);
		var.init();
		synchronized (var.minPrime){

		}
	}

	public void run4() {
		String do_smth;
		var.init(do_smth);
		var.init();
		synchronized (var.init(xyz)){

		}
	}
}