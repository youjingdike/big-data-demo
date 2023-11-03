package com.thread.concurrent.yxxy.c_026;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class T10_ScheduledPool {
	public static void main(String[] args) {
		ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
		/*ScheduledFuture sf = service.scheduleAtFixedRate(()->{
			try {
				System.out.println("start:"+Thread.currentThread().getName()+":"+LocalDateTime.now());
				TimeUnit.MILLISECONDS.sleep(4000);
				System.out.println("end:"+Thread.currentThread().getName()+":"+LocalDateTime.now());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, 0, 500, TimeUnit.MILLISECONDS);*/
		ScheduledFuture sf = service.scheduleWithFixedDelay(()->{
			try {
				System.out.println("start:"+Thread.currentThread().getName()+":"+LocalDateTime.now());
				TimeUnit.MILLISECONDS.sleep(4000);
				System.out.println("end:"+Thread.currentThread().getName()+":"+LocalDateTime.now());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, 0, 500, TimeUnit.MILLISECONDS);
	}
}
