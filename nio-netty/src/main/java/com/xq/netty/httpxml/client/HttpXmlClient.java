package com.xq.netty.httpxml.client;

import com.xq.netty.httpxml.pojo.Order;
import com.xq.netty.httpxml.handler.encode.req.HttpXmlRequestEncoder;
import com.xq.netty.httpxml.handler.decode.resp.HttpXmlResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

/**
 * 支持tcp粘包/拆包
 **/
public class HttpXmlClient {
    public void connect(int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sh) throws Exception {
                        //请求消息解码器
                        sh.pipeline().addLast("http-decoder",new HttpResponseDecoder());
                        //目的是将多个消息转换为单一的request或者response对象
                        sh.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                        sh.pipeline().addLast("xml-decoder",new HttpXmlResponseDecoder(Order.class,true));
                        sh.pipeline().addLast("http-encoder",new HttpRequestEncoder());
                        sh.pipeline().addLast("xml-encoder",new HttpXmlRequestEncoder());
                        //业务逻辑
                        sh.pipeline().addLast(new HttpXmlClientHandler());
                    }
                });

            //发起异步连接操作
            ChannelFuture f = b.connect(new InetSocketAddress(port)).sync();
            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new HttpXmlClient().connect(8080);
    }
}
