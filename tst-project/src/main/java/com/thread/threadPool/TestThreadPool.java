package com.thread.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * java有四种线程池
 * 
 * 1.可缓存线程池(newCachedThreadPool)
 * 2.定长线程(newFixedThreadPool)
 * 3.定长线程(newScheduledThreadPool)，支持定时及周期性任务执
 * 4.单线程化的线程池(newSingleThreadExecutor)
 * 
 * @author root
 *
 */
public class TestThreadPool {
	public static void main(String[] args) {
		// test1();
		// test2();
		// test3();
		// test31();
		// test4();
		test41();
	}

	/**
	 * 创建一个可缓存线程(newCachedThreadPool)，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线
	 * 线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程
	 */
	private static void test1() {
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < 10; i++) {
			final int index = i;
			try {
				Thread.sleep(index * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					System.out.println(index);
				}
			});
		}
	}

	/**
	 * 
	 * 创建一个定长线程池（newFixedThreadPool），可控制线程最大并发数，超出的线程会在队列中等待
	 * 因为线程池大小为3，每个任务输出index后sleep 2秒，所以每两秒打印3个数字
	 * 定长线程池的大小最好根据系统资源进行设置。如Runtime.getRuntime().availableProcessors()
	 */
	private static void test2() {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 10; i++) {
			final int index = i;
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						System.out.println(index);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 创建一个定长线程池(newScheduledThreadPool)，支持定时及周期性任务执行
	 * 表示延迟3秒执行
	 */
	private static void test3() {
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
		scheduledThreadPool.schedule(new Runnable() {
			public void run() {
				System.out.println("delay 3 seconds");
			}
		}, 3, TimeUnit.SECONDS);
	}

	/**
	 * 创建一个定长线程池(newScheduledThreadPool)，支持定时及周期性任务执行
	 * 表示延迟1秒后每3秒执行一次
	 */
	private static void test31() {
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out.println("delay 1 seconds, and excute every 3 seconds");
			}
		}, 1, 3, TimeUnit.SECONDS);
	}

	/**
	 * 创建一个单线程化的线程池(newSingleThreadExecutor)，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
	 * 结果依次输出，相当于顺序执行各个任务
	 */
	private static void test4() {
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (int i = 0; i < 10; i++) {
			final int index = i;
			singleThreadExecutor.execute(new Runnable() {
				public void run() {
					try {
						System.out.println(index);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 可以使用JDK自带的监控工具来监控我们创建的线程数量，运行一个不终止的线程，创建指定量的线程，来观察：
	 * 工具目录：C:\Program Files\Java\jdk1.6.0_06\bin\jconsole.exe
	 */
	private static void test41() {
		ExecutorService singleThreadExecutor = Executors.newCachedThreadPool();
		for (int i = 0; i < 100; i++) {
			final int index = i;
			singleThreadExecutor.execute(new Runnable() {
				public void run() {
					try {
						while (true) {
							System.out.println(index);
							Thread.sleep(10 * 1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}