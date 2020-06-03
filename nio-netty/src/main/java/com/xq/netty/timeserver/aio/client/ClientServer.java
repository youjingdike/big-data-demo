package com.xq.netty.timeserver.aio.client;

public class ClientServer {
    public static void main(String[] args) {
        /*
         * 实际的项目中不需要独立的线程创建异步连接对象，因为底层本身就是通过jdk的系统回调实现的。
         */
        new Thread(new AsyncTimeClientHandler("127.0.0.1",8080),"AIO-TIMECLIENT").start();
    }
}
