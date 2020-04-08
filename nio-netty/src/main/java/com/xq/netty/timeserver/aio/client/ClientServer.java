package com.xq.netty.timeserver.aio.client;

import com.xq.netty.timeserver.aio.server.AsyncTimeServerHandler;

public class ClientServer {
    public static void main(String[] args) {
        new Thread(new AsyncTimeClientHandler("127.0.0.1",8080),"AIO-TIMECLIENT").start();
    }
}
