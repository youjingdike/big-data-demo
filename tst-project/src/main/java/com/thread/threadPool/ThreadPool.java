package com.thread.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by XUGY on 2017-07-22.
 */
public class ThreadPool {
    // 定义线程池对象（用于支付业务）
//    private static ThreadPoolExecutor poolExecutor = null;
	private volatile static ThreadPool instance;
	private ThreadPool(){}
    /**
     * @Description 获取线程池对象（通过双重检查锁实现）。
     * @param
     * @return ThreadPoolExecutor 线程池对象
     * @exception @author
     *                zhehong.qiu email:qiuzhehong@tzx.com.cn
     * @version 1.0 2017-06-19
     * @see
     */
    /*public static ThreadPoolExecutor getThreadPool() {
        if(poolExecutor == null){
            synchronized (ThreadPool.class) {
                if(poolExecutor == null){
					
					 * 创建核心线程数为20，最大线程数为50(当任务队列满时才会增加大于核心线程数且小于最大线程数的线程)的线程池，使用ArrayBlockingQueue阻塞队列，队列大小为1000。
					 * 线程数超过队列大小时的策略为重试（由调用线程池的主线程自己来执行任务）。
					 
                    poolExecutor = new ThreadPoolExecutor(20,50,3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }
        return poolExecutor;
    }*/
	
	private static ThreadPool getInstance() {
		  if (instance == null) {
			synchronized (ThreadPool.class) {
				if (instance == null) {
					instance = new ThreadPool();
				}
			}
		  }
		return instance;
	  }
	
	private ThreadPoolExecutor getPool() {
    	return TaskThreadPoolExecutor.getThreadPool(this.getClass().getName(), 
				20,50,3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
	
    public static ThreadPoolExecutor getThreadPool() {
    	return ThreadPool.getInstance().getPool();
    }
}
