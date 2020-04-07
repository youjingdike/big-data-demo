package com.xq.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * java序列化
 **/
public class WebSocketServer {

    public void run(int port) throws CertificateException, SSLException {
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
                        ChannelPipeline pipeline = sh.pipeline();
                        pipeline.addLast("http-codec",new HttpServerCodec());
                        //目的是将多个消息转换为单一的request或者response对象
                        pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                        //目的是支持异步大文件传输
                        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                        //业务逻辑
                        pipeline.addLast("handler",new WebSocketServerHandler());
                    }
                });
            //绑定端口，同步等待成功
            Channel channel = b.bind(port).sync().channel();
            System.out.println("websocket server is started at port:"+port+".");
            System.out.println("open your browser and navigate to http://localhost:"+port+"/");
            //等待服务端监听端口关闭
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws CertificateException, SSLException {
        new WebSocketServer().run(8080);
    }
}
