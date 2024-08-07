package com.xq.netty.httpxml.codec.resp;

import com.xq.netty.httpxml.codec.AbstractHttpXmlDecoder;
import com.xq.netty.httpxml.vo.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

    public HttpXmlResponseDecoder(Class<?> clazz) {
        this(clazz,false);
    }

    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out) throws Exception {
        HttpXmlResponse xmlResponse = new HttpXmlResponse(msg, decode0(ctx, msg.content()));
        out.add(xmlResponse);
    }
}
