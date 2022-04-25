package com.thread.synchr;
public class TestThread {
    public static void main(String[] args) { 
       TxtThread tt = new TxtThread(); 
       new Thread(tt,"t1").start(); 
       new Thread(tt,"t2").start(); 
       new Thread(tt,"t3").start(); 
       new Thread(tt,"t4").start(); 
    } 
}

class TxtThread implements Runnable { 
    int num = 100; 
    String str = new String();
    
    public void run() { 
        synchronized (str) { 
            while (num > 0) {
                try { 
                    Thread.sleep(1000); 
                } catch (Exception e) { 
                    e.getMessage(); 
                } 
                System.out.println(Thread.currentThread().getName() 
                        + ",this is " + num--); 
            }
            if (num == 0) {
                System.out.println(Thread.currentThread().getName() 
                        + ",end");
            }
        } 
            //不加锁
           /*while (num > 0) {
               try { 
                   Thread.sleep(1000); 
               } catch (Exception e) { 
                   e.getMessage(); 
               } 
               System.out.println(Thread.currentThread().getName() 
                       + ",this is " + num--); 
           }
           if (num == 0) {
               System.out.println(Thread.currentThread().getName() 
                       + ",end");
           }*/
        
    } 
}
