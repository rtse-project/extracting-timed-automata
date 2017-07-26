class Thread_2 extends Thread {
	Object lock;
	Thread_2(Object lock){
		this.lock = lock;
	}
	public void run(){
		var init = this.init();
		synchronized(lock){
			System.out.print("Thread2");
		}
		System.out.print("End");
	}
	public synchronized int init(){
		return this.hashCode();
	}
}