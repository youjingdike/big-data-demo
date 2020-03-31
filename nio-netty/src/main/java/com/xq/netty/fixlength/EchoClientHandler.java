package com.xq.netty.fixlength;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClientHandler extends ChannelHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

    private int counter;
    private String[] msgs = new String[] {
      "AAAAAAAA",
      "AAAAA",
      "AAAAAAAA",
      "BBBBBBBBBBBBBBBBBBBB",
      "CCCCCCCCCCCCCCCCCCCCCCCCCCC",
      "DDDFFFFFFFFF"
    };
    public EchoClientHandler() {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < msgs.length; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(msgs[i].getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("This is : " + ++counter +" times receive server :["+ msg +"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
