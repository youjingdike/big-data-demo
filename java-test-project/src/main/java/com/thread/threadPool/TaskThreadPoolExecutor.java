package com.thread.threadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 定时任务公共线程池
 * @author yanZhen
 *
 */
public class TaskThreadPoolExecutor {
	
	
	//这个没必要加volatile
//	private static volatile   ConcurrentHashMap<String, ThreadPoolExecutor> hashmap  = new ConcurrentHashMap<String, ThreadPoolExecutor>();
	private static ConcurrentHashMap<String, ThreadPoolExecutor> hashmap  = new ConcurrentHashMap<String, ThreadPoolExecutor>();
	
	/**
	 * 单例获取线程池对象
	 * className 请在非静态方法中使用this.getClass().getName()来获取唯一class名称
	 * corePoolSize  线程池的基本大小
       maximumPoolSize,线程池中允许的最大线程数
       keepAliveTime,线程空闲后存活时间   
       unit, keepAliveTime时间单位 例如TimeUnit.SECONDS
       BlockingQueue<Runnable> workQueue,任务队列
       handler 线程池对拒绝任务的处理策略 
	 * @return 
	 */
	public static ThreadPoolExecutor getThreadPool(String className,int corePoolSize,
            int maximumPoolSize,long keepAliveTime, TimeUnit unit,BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
		System.out.println(className);
		ThreadPoolExecutor threadPoolExecutor = hashmap.get(className);
		if(threadPoolExecutor == null){
			synchronized (TaskThreadPoolExecutor.class) {
				/*
				 * 在这里应该再去读取并判断一下是否为null，如果不重新读取，那肯定还是null，只读取一次并为null，
				 * 这时多个线程过来有可能都判断为null，开始等待锁，先获得锁的先放了一个，后面的就不需要放了，
				 * 但是这种写法后面的线程同样会创建线程池,这些池在代码里是没有销毁的。有可能产生问题。
				 */
				threadPoolExecutor = hashmap.get(className);
				if(threadPoolExecutor == null){
					threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit, workQueue,
							handler);
					hashmap.putIfAbsent(className, threadPoolExecutor);
				}
			}
		}
		
		System.out.println(hashmap.size());
		return threadPoolExecutor;
	}
	

}
