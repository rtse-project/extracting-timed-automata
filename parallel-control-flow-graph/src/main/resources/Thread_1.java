class Thread_1 extends Thread {
	Object lock;
	Thread_2 var;
	Thread_1(Object lock){
		this.lock = lock;
	}
	public void run(){
		int init = var.init();
		if(x > 0){
			print("a");
		} else {
			println("b");
		}
		synchronized(lock){
			System.out.print("Thread1");
		}
		Thread.sleep(1000);
		System.out.print("End");
	}
}