package com.xq.netty.timeserver.aio.server;

import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        if (this.channel==null) {
            this.channel = channel;
        }
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] bytes = new byte[attachment.remaining()];
        attachment.get(bytes);
        String req = new String(bytes, CharsetUtil.UTF_8);
        System.out.println("The time server receive order is:"+req );
        String curTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date(System.currentTimeMillis()).toString() : "BAND ORDER";
        try {
            doWrite(curTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String currentTime) throws IOException {
        if (currentTime!=null && currentTime.trim().length()>0) {
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {//attachment就是writeBuffer
                    //如果没有发生完成，继续发送
                    if (attachment.hasRemaining()) {
                        channel.write(writeBuffer,writeBuffer,this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    /**
     * @param exc
     * @param attachment
     * 这里可以对异常进行判断，如果是I/O异常，就关闭连接，释放资源，如果是其他异常，就按自己的业务逻辑处理
     */
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

        try {
            if (exc instanceof IOException) {
                this.channel.close();
            } else {
                //业务处理
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
