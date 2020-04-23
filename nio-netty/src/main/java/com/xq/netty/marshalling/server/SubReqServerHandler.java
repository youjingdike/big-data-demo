package com.xq.netty.marshalling.server;

import com.xq.netty.marshalling.pojo.SubscribeReq;
import com.xq.netty.marshalling.pojo.SubscribeResp;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter {
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Server accept client subscribe req : [" + req.toString()+"]");
            ctx.writeAndFlush(resq(req.getSubReqID()));
        }
    }

    private SubscribeResp resq(int subReqID) {
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(subReqID);
        resp.setRespCode(0);
        resp.setDesc("Netty book order succeed,3 days later,send to the designated address");
        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();//发生异常，关闭链路
    }
}
