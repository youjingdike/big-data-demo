package com.xq.netty.timeserver.aio.server1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class TimeServerHandler {
    private int port;
    CountDownLatch latch;

    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public TimeServerHandler(int port) {
        this.port = port;

        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doAccept() {
        latch = new CountDownLatch(1);
        try {
            asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
            /*阻塞线程，防止服务端执行完成退出
             */
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
