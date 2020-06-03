package com.xq.netty.timeserver.aio.client;

public class ClientServer {
    public static void main(String[] args) {
        new Thread(new AsyncTimeClientHandler("127.0.0.1",8080),"AIO-TIMECLIENT").start();
    }
}
