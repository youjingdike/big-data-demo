/**
 * http://blog.csdn.net/sunxianghuang/article/details/52221913 
 * http://www.educity.cn/java/498061.html
 * 阅读concurrentskiplistmap
 */
package com.thread.concurrent.yxxy.c_025;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class T01_ConcurrentMap {
	public static void main(String[] args) {
		Map<String, String> map = new ConcurrentHashMap<>();
//		Map<String, String> map = new ConcurrentSkipListMap<>(); //高并发并且排序

//		Map<String, String> map = new Hashtable<>();
//		Map<String, String> map = new HashMap<>(); //Collections.synchronizedXXX
		//TreeMap
		Random r = new Random();
		Thread[] ths = new Thread[100];
		CountDownLatch latch = new CountDownLatch(ths.length);
		long start = System.currentTimeMillis();
		for(int i=0; i<ths.length; i++) {
			ths[i] = new Thread(()->{
				for(int j=0; j<10000; j++) map.put("a" + r.nextInt(100000), "a" + r.nextInt(100000));
				latch.countDown();
			});
		}

		Arrays.asList(ths).forEach(t->t.start());
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		System.out.println(end - start);
		ConcurrentMap<String,String> map1 = new ConcurrentHashMap<>();
		map1.put("1","1");
		Map<String, String> map2 = Collections.unmodifiableMap(map1);
		System.out.println(map1);
		System.out.println(map2);

		map1.put("1","2");
		System.out.println(map1);
		System.out.println(map2);
	}
}
