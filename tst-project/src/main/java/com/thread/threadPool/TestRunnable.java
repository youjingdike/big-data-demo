package com.thread.threadPool;

public class TestRunnable implements Runnable {
	private int i = 0;
	public TestRunnable(int i) {
		this.i = i;
	}
	@Override
	public void run() {
		long s = System.currentTimeMillis();
		try {
			if ("main".equals(Thread.currentThread().getName())) {
				Thread.sleep(100);
			} else {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long e = System.currentTimeMillis();
		System.out.println(Thread.currentThread().getName()+":"+this.i+",处理的。。。,处理时间："+(e-s)+"ms");
	}
}
