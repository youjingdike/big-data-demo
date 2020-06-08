package com.xq.netty.httpxml.server;

import com.xq.netty.httpxml.bo.Order;
import com.xq.netty.httpxml.codec.req.HttpXmlRequestDecoder;
import com.xq.netty.httpxml.codec.resp.HttpXmlResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;

/**
 * java序列化
 **/
public class HttpXmlServer {

    public void run(final int port) throws Exception {
        //配置服务端的nio线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sh) throws Exception {
                        //请求消息解码器
                        sh.pipeline().addLast("http-decoder",new HttpRequestEncoder());
                        //目的是将多个消息转换为单一的request或者response对象
                        sh.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                        sh.pipeline().addLast("xml-decoder",new HttpXmlRequestDecoder(Order.class,true));
                        sh.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                        sh.pipeline().addLast("xml-encoder",new HttpXmlResponseEncoder());
                        //业务逻辑
                        sh.pipeline().addLast(new HttpXmlServerHandler());
                    }
                });
            //绑定端口，同步等待成功
            ChannelFuture future = b.bind(new InetSocketAddress(port)).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new HttpXmlServer().run(8080);
    }
}
