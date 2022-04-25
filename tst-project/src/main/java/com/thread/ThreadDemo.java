package com.thread;

public class ThreadDemo {

	public static void main(String[] args) {
		 Thread t = Thread.currentThread();
	     t.setName("Admin Thread");
	     // set thread priority to 1
	     t.setPriority(1);
	     
	     // prints the current thread
	     System.out.println("Thread = " + t);
	    
	     for (int i=0;i<100;i++) {
	    	 new Thread(){
	    		 public void run() {
	    			 System.out.println("ddd");
	    		 };
	    	 }.start();
	     }
	     
	     int count = Thread.activeCount();
	     System.out.println("currently active threads = " + count);
	    
	     Thread th[] = new Thread[count];
	     // returns the number of threads put into the array 
	     Thread.enumerate(th);
	    
	     // prints active threads
	     for (int i = 0; i < count; i++) {
	        System.out.println(i + ": " + th[i]);
	     }
	}

}
