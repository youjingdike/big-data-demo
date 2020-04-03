package com.xq.netty.javaseri.client;

import com.xq.netty.javaseri.server.SubscribeReq;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubReqClientHandler extends ChannelHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(SubReqClientHandler.class);

    public SubReqClientHandler() {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(subReq(i));
        }
    }

    private SubscribeReq subReq(int reqID) {
        SubscribeReq req = new SubscribeReq();
        req.setSubReqID(reqID);
        req.setUserName("Lilinfeng");
        req.setAddress("XXXXXXXXXXXXXXXXXXXX");
        req.setProductName("Netty 权威指南");
        req.setPhoneNumber("123456788");
        return req;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server resp:["+ msg +"]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
