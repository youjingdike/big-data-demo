package com.xq.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * java序列化
 **/
public class HttpFileServer {

    private static final String DEFAULT_URL = "/";
    private static final String ROOT = "E:\\Music";

    public void run(final int port,final String url,final String root) throws CertificateException, SSLException {
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
//                        sh.pipeline().addLast(new HttpServerCodec());
                        sh.pipeline().addLast(new HttpRequestDecoder());
                        //目的是将多个消息转换为单一的request或者response对象
                        sh.pipeline().addLast(new HttpObjectAggregator(65536));
                        sh.pipeline().addLast(new HttpResponseEncoder());
                        //目的是支持异步大文件传输
                        sh.pipeline().addLast(new ChunkedWriteHandler());
                        //业务逻辑
                        sh.pipeline().addLast(new HttpFileServerHandler(url,root));
                    }
                });
            //绑定端口，同步等待成功
//            String host = "127.0.0.1";
            String host = "192.168.1.11";
            ChannelFuture future = b.bind(host,port).sync();
            System.out.println("File server is start, url is: http://"+host+":"+port+url);
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

    public static void main(String[] args) throws CertificateException, SSLException {
        new HttpFileServer().run(8080,DEFAULT_URL,ROOT);
    }
}
