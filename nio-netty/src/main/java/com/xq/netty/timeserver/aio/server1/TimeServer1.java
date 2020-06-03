package com.xq.netty.timeserver.aio.server1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class TimeServer1 {

    public static void main(String[] args) {
        new TimeServerHandler(8080).doAccept();
    }
}
