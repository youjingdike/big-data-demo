package com.xq.netty.timeserver.aio.server;

public class TimeServer {
    public static void main(String[] args) {
        AsyncTimeServerHandler asynTimeServerHandler = new AsyncTimeServerHandler(8080);
        new Thread(asynTimeServerHandler,"AIO-TIMESERVER").start();
    }
}
