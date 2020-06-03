package com.xq.netty.timeserver.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        /*
        * read的三个参数的含义：
        * ByteBuffer dst：接收缓冲区，用于从异步channel中读取数据包;
        * A attachment：异步channel携带的附近，通知回调的时候作为入参使用;
        * CompletionHandler<Integer,? super A> handler：接收回调通知的业务handler，如：ReadCompletionHandler.
         */
        result.read(byteBuffer,byteBuffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
