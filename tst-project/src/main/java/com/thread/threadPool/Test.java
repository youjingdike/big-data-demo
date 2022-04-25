package com.thread.threadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Test implements Runnable {
	public void tst() {
		ThreadPoolExecutor executor = TaskThreadPoolExecutor.getThreadPool(this.getClass().getName(),
                5, 50, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200),
                new ThreadPoolExecutor.CallerRunsPolicy() );
		System.out.println("executor:"+executor);
	}
	
	public void tst2() {
		/*ThreadPoolExecutor executor = TaskThreadPoolExecutor.getThreadPool(this.getClass().getName(),
                5, 5, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100),
                new ThreadPoolExecutor.AbortPolicy());
		try {
			executor.execute(a);
		} catch (RejectedExecutionException e) {
			System.out.println(Thread.currentThread()+":"+i+",报错了。。。");
		}*/
		ThreadPoolExecutor executor = TaskThreadPoolExecutor.getThreadPool(this.getClass().getName(),
				5, 5, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
				new ThreadPoolExecutor.CallerRunsPolicy() );
		
		System.out.println("executor:"+executor);
		for (int i=0;i<100000;i++ ){
			System.out.println(Thread.currentThread().getName()+"打印的i:"+i+",sttt");
			/*if (i%2==0) {
				TestRunnable a = new TestRunnable(i);
				try {
					executor.execute(a);
				} catch (RejectedExecutionException e) {
					System.out.println(Thread.currentThread()+":"+i+",报错了。。。");
				}
			} else {
				System.out.println(i+",不用线程池处理...");
			}*/
			TestRunnable a = new TestRunnable(i);
			executor.execute(a);
			System.out.println(Thread.currentThread().getName()+"打印的i:"+i+",eddd");
		}
		System.out.println("!!!!");
		executor.shutdown();
		System.out.println("@@@");
	}
	
	public void tst1() {
		ThreadPoolExecutor executor = ThreadPool.getThreadPool();
		System.out.println("executor:"+executor);
	}
	
	public static void main(String[] args) {
//		Test test = new Test();
//		test.tst2();
//		/*for (int i=0;i<50;i++) {
//			new Thread(test).start();
//		}*/
//		ThreadPoolExecutor executor = ThreadPool.getThreadPool();
//		ExecutorCompletionService<String> executorCompletionService = new ExecutorCompletionService<>(executor);
		Animal animal = new Animal();
		animal.setName("test1");
		Animal animal1 = new Animal();
		animal1.setName("test3");
		List<Animal> animalList = new ArrayList<>();
		animalList.add(animal1);
		animalList.add(animal);
		animalList.forEach(System.out::println);
		SortedSet<Animal> animalSortedSet = new TreeSet<Animal>((o1,o2)->{
			int i = 0;
			int comp = o1.getName().compareTo(o2.getName());
			if (comp > 0) {
				i = -1;
			} else if (comp < 0) {
				i = 1;
			}
			return i;
		});
		animalSortedSet.addAll(animalList);
		animal1.setName("sort");
		animalSortedSet.forEach(System.out::println);
	}
	
	@Override
	public void run() {
		tst();
		tst1();

	}

	private static class Animal{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "name:"+name;
		}
	}
}
