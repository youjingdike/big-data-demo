/**
 * 设计4个线程，其中两个线程每次对j增加1，另外两个每次对j减少1。
 */
package com.thread;

/**
 * @author Administrator
 */
public class ThreadTest {
    private int j;
    
    public static void main(String[] args) {
        ThreadTest threadTest = new ThreadTest();
        Inc inc = threadTest.new Inc();
        Dec dec = threadTest.new Dec();
        for (int i=0; i<2; i++) {
            new Thread(inc).start();
            new Thread(dec).start();
        }
    }
    
    private synchronized void inc() {
        j++;
        System.out.println(Thread.currentThread().getName()+ "-inc:" + j);
    }
    private synchronized void dec() {
        j--;
        System.out.println(Thread.currentThread().getName()+ "-dec:" + j);
    }
    
    class Inc implements Runnable {
        
        public void run() {
            for (int i=0; i<100; i++) {
                inc();
            }
        }
        
    }
    
    class Dec implements Runnable {

        public void run() {
            for (int i=0; i<100; i++) {
                dec();
            }
        }
        
    }
}
