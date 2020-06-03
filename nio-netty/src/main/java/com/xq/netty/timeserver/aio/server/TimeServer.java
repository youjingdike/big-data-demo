package com.xq.netty.timeserver.aio.server;

public class TimeServer {
    public static void main(String[] args) {
        AsyncTimeServerHandler asynTimeServerHandler = new AsyncTimeServerHandler(8080);
        /*
         *实际项目中，不需要启动独立的线程来处理AsynchronousServerSocketChannel，
         * 这里只是demo
         */
        new Thread(asynTimeServerHandler,"AIO-TIMESERVER").start();
    }
}
