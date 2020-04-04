package com.xq.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * java序列化
 **/
public class HttpFileServer {

    private static final String DEFAULT_URL = "/";
    private static final String ROOT = "E:\\Music";

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

    public void run(final int port,final String url,final String root) throws CertificateException, SSLException {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext(ssc.certificate(),
                    ssc.privateKey());
        } else {
            sslCtx = null;
        }
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
                        if (sslCtx != null) {
                            sh.pipeline().addLast(sslCtx.newHandler(sh.alloc()));
                        }
                        //请求消息解码器
                        sh.pipeline().addLast("http-decoder", new HttpServerCodec());
                        //目的是将多个消息转换为单一的request或者response对象
                        sh.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                        //目的是支持异步大文件传输
                        sh.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                        //业务逻辑
                        sh.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url,root));
                    }
                });
            //绑定端口，同步等待成功
            ChannelFuture future = b.bind("127.0.0.1",port).sync();
            System.out.println("File server is start, url is: http://127.0.0.1:"+port+url);
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
