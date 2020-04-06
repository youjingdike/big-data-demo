package com.xq.netty.httpxml.client;

import com.xq.netty.httpxml.request.HttpXmlRequest;
import com.xq.netty.httpxml.response.HttpXmlResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpXmlRequest httpXmlRequest = new HttpXmlRequest(null, OrderFactory.create(123));
        ctx.writeAndFlush(httpXmlRequest);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
        System.out.println("The cient receive response of http header is : " +
                msg.getResponse().headers().names());
        System.out.println("The cient receive response of http body is : " +
                msg.getResult());
    }
}
