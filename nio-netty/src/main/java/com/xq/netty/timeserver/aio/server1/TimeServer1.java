package com.xq.netty.timeserver.aio.server1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class TimeServer1 {
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public void start(){
        int port = 8080;
        latch = new CountDownLatch(1);
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port:"+port);;
            asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
            try {
                /*阻塞线程，防止服务端执行完成退出
                 */
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public static void main(String[] args) {
        new TimeServer1().start();
    }
}
