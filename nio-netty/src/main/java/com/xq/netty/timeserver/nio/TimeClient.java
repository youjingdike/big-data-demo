package com.xq.netty.timeserver.nio;

public class TimeClient {
    public static void main(String[] args) {
        TimeClientHandle clientHandle = new TimeClientHandle("127.0.0.1", 8080);
        new Thread(clientHandle, "NIO-TIMECLIENT").start();
    }
}
